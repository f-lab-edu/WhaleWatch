package com.whalewatch.telegram;

import com.whalewatch.dto.ThresholdEventDto;
import com.whalewatch.dto.UserRegistrationEventDto;
import com.whalewatch.redis.RedisStateService;
import com.whalewatch.redis.RegistrationData;
import com.whalewatch.redis.ThresholdSettingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class TelegramWebhookUserBot extends TelegramWebhookBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramWebhookUserBot.class);

    private final TelegramBotProperties telegramBotProperties;
    private final RedisStateService redisStateService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TelegramWebhookUserBot(TelegramBotProperties telegramBotProperties,
                                  RedisStateService redisStateService,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.telegramBotProperties = telegramBotProperties;
        this.redisStateService = redisStateService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }

    @Override
    public String getBotPath() {
        return "/telegram/webhook";
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            if (!update.hasMessage() || !update.getMessage().hasText()) {
                log.warn("Received update without text or message");
                return null;
            }
            String messageText = update.getMessage().getText().trim();
            Long chatId = update.getMessage().getChatId();
            log.info("Received update from chatId {}: {}", chatId, messageText);

            if (messageText.equalsIgnoreCase("/start")) {
                redisStateService.deleteRegistrationData(chatId);
                redisStateService.deleteThresholdData(chatId);

                RegistrationData regData = new RegistrationData();
                redisStateService.saveRegistrationData(chatId, regData);
                sendTextMessage(chatId, "Welcome! Please enter your email to sign up.");
                log.info("Processed /start command for chatId {}", chatId);
                return null;
            } else if (messageText.equalsIgnoreCase("/set_threshold")) {
                redisStateService.deleteRegistrationData(chatId);
                redisStateService.deleteThresholdData(chatId);

                // threshold 설정 진행
                ThresholdSettingData thresholdData = new ThresholdSettingData();
                redisStateService.saveThresholdData(chatId, thresholdData);
                sendTextMessage(chatId, "Enter Coins (BTC, ETH, SOL):");
                return null;
            }

            // 회원가입 처리
            RegistrationData regData = redisStateService.getRegistrationData(chatId);
            if (regData != null) {
                if (regData.getEmail() == null) {
                    regData.setEmail(messageText);
                    redisStateService.saveRegistrationData(chatId, regData);
                    sendTextMessage(chatId, "Email received. Now, please enter your username.");
                } else if (regData.getUsername() == null) {
                    regData.setUsername(messageText);
                    // Kafka에 "user_registration_topic" 전송
                    kafkaTemplate.send("user_registration_topic",
                            new UserRegistrationEventDto(
                                    chatId,
                                    regData.getEmail(),
                                    regData.getUsername()
                            )
                    );
                    sendTextMessage(chatId, "Registration request sent! Please wait for confirmation.");
                    redisStateService.deleteRegistrationData(chatId);
                }
                return null;
            }

            // 임계값 설정 처리
            ThresholdSettingData thresholdData = redisStateService.getThresholdData(chatId);
            if (thresholdData != null) {
                if (thresholdData.getCoin() == null) {
                    String coinInput = messageText.toUpperCase();
                    if (!("BTC".equals(coinInput) || "ETH".equals(coinInput) || "SOL".equals(coinInput))) {
                        sendTextMessage(chatId, "Enter a valid coin (BTC, ETH, SOL):");
                        return null;
                    }
                    thresholdData.setCoin(coinInput);
                    redisStateService.saveThresholdData(chatId, thresholdData);
                    sendTextMessage(chatId, "Please enter a threshold:");
                    return null;
                } else if (thresholdData.getThreshold() == null) {
                    try {
                        double thresholdVal = Double.parseDouble(messageText);
                        thresholdData.setThreshold(thresholdVal);
                        // Kafka에 "user_threshold_topic" 전송
                        kafkaTemplate.send("user_threshold_topic",
                                new ThresholdEventDto(
                                        chatId,
                                        thresholdData.getCoin(),
                                        thresholdVal
                                )
                        );
                        sendTextMessage(chatId, "Threshold request sent! Please wait for confirmation.");
                    } catch (NumberFormatException e) {
                        sendTextMessage(chatId, "Enter the threshold again:");
                        return null;
                    } finally {
                        redisStateService.deleteThresholdData(chatId);
                    }
                    return null;
                }
            }

            // 기본 안내 메시지 전송
            sendTextMessage(chatId, "Please type /start to begin registration or /set_threshold to set alert threshold.");
            return null;
        } catch (Exception e) {
            log.error("Error in onWebhookUpdateReceived: {}", e.getMessage(), e);
            return null; // Telegram은 200 OK 응답을 기대하므로, 예외가 발생해도 null 반환
        }
    }

    public void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        log.info("Attempting to send message to chatId {}: {}", chatId, text);

        try {
            execute(message);
            log.info("Message sent successfully to chatId {}.", chatId);
        } catch (TelegramApiException e) {
            log.error("Error sending message to chatId {}: {}", chatId, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}