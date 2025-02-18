package com.whalewatch.telegram;

import com.whalewatch.config.TelegramBotProperties;
import com.whalewatch.domain.AlertSetting;
import com.whalewatch.domain.User;
import com.whalewatch.service.AlertService;
import com.whalewatch.service.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class TelegramUserBot extends TelegramLongPollingBot {

    private final UserService userService;
    private final AlertService alertService;
    private final TelegramBotProperties telegramBotProperties;

    private Map<Long, RegistrationData> registrationDataMap = new HashMap<>();
    private Map<Long, ThresholdSettingData> thresholdDataMap = new HashMap<>();

    public TelegramUserBot(UserService userService, AlertService alertService, TelegramBotProperties telegramBotProperties) {
        this.userService = userService;
        this.alertService = alertService;
        this.telegramBotProperties = telegramBotProperties;
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
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            // set_threshold 명령어 처리
            if (messageText.equalsIgnoreCase("/set_threshold")) {
                thresholdDataMap.put(chatId, new ThresholdSettingData());
                sendTextMessage(chatId, "Enter Coins (BTC, ETH, SOL):");
                return;
            }

            // 임계값 설정 진행
            if (thresholdDataMap.containsKey(chatId)) {
                ThresholdSettingData data = thresholdDataMap.get(chatId);
                // 코인 입력 단계
                if (data.getCoin() == null) {
                    String coinInput = messageText.trim().toUpperCase();
                    if (!("BTC".equals(coinInput) || "ETH".equals(coinInput) || "SOL".equals(coinInput))) {
                        sendTextMessage(chatId, "Enter a valid coin (BTC, ETH, SOL):");
                        return;
                    }
                    data.setCoin(coinInput);
                    sendTextMessage(chatId, "Please enter a threshold:");
                    return;
                }
                // 임계값 입력 단계
                else if (data.getThreshold() == null) {
                    try {
                        double threshold = Double.parseDouble(messageText.trim());
                        data.setThreshold(threshold);

                        User user = userService.findByTelegramChatId(chatId);

                        AlertSetting alertSetting = new AlertSetting(data.getCoin(), threshold, false);
                        alertSetting.setUserId(user.getId());
                        alertService.createAlert(alertSetting);
                        sendTextMessage(chatId, "Threshold set: Coin:" + data.getCoin() + ", Threshold: " + threshold);
                    } catch (NumberFormatException e) {
                        sendTextMessage(chatId, "Enter the threshold again:");
                        return;
                    } finally {
                        thresholdDataMap.remove(chatId);
                    }
                    return;
                }
            }

            // start 명령어 처리
            if (messageText.equalsIgnoreCase("/start")) {
                registrationDataMap.put(chatId, new RegistrationData());
                sendTextMessage(chatId, "Welcome! Please enter your email to sign up.");
                return;
            }

            RegistrationData regData = registrationDataMap.get(chatId);
            if (regData != null) {
                if (regData.getEmail() == null) {
                    regData.setEmail(messageText.trim());
                    sendTextMessage(chatId, "Email received. Now, please enter your username.");
                } else if (regData.getUsername() == null) {
                    regData.setUsername(messageText.trim());
                    User newUser = new User(regData.getEmail(), regData.getUsername());
                    newUser.setTelegramChatId(chatId);
                    userService.registerUser(newUser);
                    sendTextMessage(chatId, "Registration completed! You can request an OTP to log in.");
                    registrationDataMap.remove(chatId);
                }
                return;
            }
            // ----------------------------------

            sendTextMessage(chatId, "Unrecognized command. Please type /start to begin registration or /set_threshold to set alert threshold.");
        }
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

    @EventListener
    public void handleTelegramMessageEvent(TelegramMessageEvent event) {
        sendTextMessage(event.getChatId(), event.getMessage());
    }

    // 회원가입 설정용
    private static class RegistrationData {
        private String email;
        private String username;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    //  임계값 설정용
    private static class ThresholdSettingData {
        private String coin;
        private Double threshold;

        public String getCoin() {
            return coin;
        }
        public void setCoin(String coin) {
            this.coin = coin;
        }
        public Double getThreshold() {
            return threshold;
        }
        public void setThreshold(Double threshold) {
            this.threshold = threshold;
        }
    }
}
