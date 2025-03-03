package com.whalewatch.telegram;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class TelegramWebhookController {

    private final TelegramWebhookUserBot telegramWebhookUserBot;

    public TelegramWebhookController(TelegramWebhookUserBot telegramWebhookUserBot) {
        this.telegramWebhookUserBot = telegramWebhookUserBot;
    }

    @PostMapping("/telegram/webhook")
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update) {
        // TelegramWebhookUserBot 내부의 onWebhookUpdateReceived()를 호출
        telegramWebhookUserBot.onWebhookUpdateReceived(update);
        // Telegram은 HTTP 200 OK , 빈 200 응답을 반환합니다.
        return ResponseEntity.ok().build();
    }
}
