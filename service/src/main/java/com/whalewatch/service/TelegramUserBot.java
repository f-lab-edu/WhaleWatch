package com.whalewatch.service;

import com.whalewatch.config.TelegramBotProperties;
import com.whalewatch.domain.User;
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
    private final TelegramBotProperties telegramBotProperties;

    private Map<Long, RegistrationData> registrationDataMap = new HashMap<>();

    public TelegramUserBot(UserService userService, TelegramBotProperties telegramBotProperties) {
        this.userService = userService;
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

            if (messageText.equalsIgnoreCase("/start")) {
                registrationDataMap.put(chatId, new RegistrationData());
                sendTextMessage(chatId, "Welcome! Please enter your email to sign up.");
                return;
            }

            RegistrationData data = registrationDataMap.get(chatId);
            if (data != null) {
                if (data.getEmail() == null) {
                    data.setEmail(messageText.trim());
                    sendTextMessage(chatId, "Email received. Now, please enter your username.");
                } else if (data.getUsername() == null) {
                    data.setUsername(messageText.trim());
                    User newUser = new User(data.getEmail(), data.getUsername());
                    newUser.setTelegramChatId(chatId);
                    userService.registerUser(newUser);
                    sendTextMessage(chatId, "Registration completed! You can now request an OTP to log in.");
                    registrationDataMap.remove(chatId);
                }
                return;
            }
            sendTextMessage(chatId, "Unrecognized command. Please type /start to begin registration.");
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

    // 내부 대화 상태 저장 클래스
    private static class RegistrationData {
        private String email;
        private String username;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}
