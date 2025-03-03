package com.whalewatch.telegram;

import com.whalewatch.config.TelegramBotProperties;
import com.whalewatch.domain.AlertSetting;
import com.whalewatch.domain.User;
import com.whalewatch.service.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class TelegramWebhookUserBot extends TelegramWebhookBot {

    private final UserService userService;
    private final AlertService alertService;
    private final TelegramBotProperties telegramBotProperties;
    private final RedisStateService redisStateService;

    public TelegramWebhookUserBot(UserService userService,
                                  AlertService alertService,
                                  TelegramBotProperties telegramBotProperties,
                                  RedisStateService redisStateService) {
        this.userService = userService;
        this.alertService = alertService;
        this.telegramBotProperties = telegramBotProperties;
        this.redisStateService = redisStateService;
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
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }
        String messageText = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();

        if (messageText.equalsIgnoreCase("/start")) {
            // /start를 시작하면 모두 초기화
            redisStateService.deleteRegistrationData(chatId);
            redisStateService.deleteThresholdData(chatId);

            try {
                userService.findByTelegramChatId(chatId);
                sendTextMessage(chatId, "You are already registered.");
            } catch (RuntimeException e) {
                RegistrationData regData = new RegistrationData();
                redisStateService.saveRegistrationData(chatId, regData);
                sendTextMessage(chatId, "Welcome! Please enter your email to sign up.");
            }
            return null;
        } else if (messageText.equalsIgnoreCase("/set_threshold")) {
            // /set_threshold 실행시 모두 초기화
            redisStateService.deleteRegistrationData(chatId);
            redisStateService.deleteThresholdData(chatId);

            try {
                // 등록된 사용자만 임계값 설정 가능
                userService.findByTelegramChatId(chatId);
                ThresholdSettingData thresholdData = new ThresholdSettingData();
                redisStateService.saveThresholdData(chatId, thresholdData);
                sendTextMessage(chatId, "Enter Coins (BTC, ETH, SOL):");
            } catch (RuntimeException e) {
                sendTextMessage(chatId, "Please register first using /start.");
            }
            return null;
        }

        // 회원가입 먼저
        RegistrationData regData = redisStateService.getRegistrationData(chatId);
        if (regData != null) {
            if (regData.getEmail() == null) {
                regData.setEmail(messageText);
                redisStateService.saveRegistrationData(chatId, regData);
                sendTextMessage(chatId, "Email received. Now, please enter your username.");
            } else if (regData.getUsername() == null) {
                regData.setUsername(messageText);
                User newUser = new User(regData.getEmail(), regData.getUsername());
                newUser.setTelegramChatId(chatId);
                userService.registerUser(newUser);
                sendTextMessage(chatId, "Registration completed! You can request an OTP to log in.");
                redisStateService.deleteRegistrationData(chatId);
            }
            return null;
        }

        // 임계값 설정
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
                    double threshold = Double.parseDouble(messageText);
                    thresholdData.setThreshold(threshold);
                    User user = userService.findByTelegramChatId(chatId);
                    if (user == null) {
                        sendTextMessage(chatId, "You must register first using /start before setting a threshold.");
                        redisStateService.deleteThresholdData(chatId);
                        return null;
                    }
                    AlertSetting alertSetting = new AlertSetting(thresholdData.getCoin(), threshold, false);
                    alertSetting.setUserId(user.getId());
                    alertService.createAlert(alertSetting);
                    sendTextMessage(chatId, "Threshold set: Coin: " + thresholdData.getCoin() + ", Threshold: " + threshold);
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
    }



    public void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @org.springframework.context.event.EventListener
    public void handleTelegramMessageEvent(TelegramMessageEvent event) {
        sendTextMessage(event.getChatId(), event.getMessage());
    }
}