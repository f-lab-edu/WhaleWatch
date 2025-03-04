package com.whalewatch.telegram;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Configuration
public class TelegramBotConfig {
    private static final Logger log = LoggerFactory.getLogger(TelegramWebhookUserBot.class);

    private final TelegramBotProperties telegramBotProperties;
    private final TelegramWebhookUserBot telegramWebhookUserBot;

    public TelegramBotConfig(TelegramBotProperties telegramBotProperties, TelegramWebhookUserBot telegramWebhookUserBot) {
        this.telegramBotProperties = telegramBotProperties;
        this.telegramWebhookUserBot = telegramWebhookUserBot;
    }


    @Bean
    public TelegramBotsApi telegramBotsApi() throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        String webhookUrl = telegramBotProperties.getWebhookUrl();
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            throw new IllegalStateException("telegram.bot.webhookUrl property is not set or is empty");
        }
        SetWebhook setWebhook = SetWebhook.builder().url(webhookUrl).build();

        botsApi.registerBot(telegramWebhookUserBot, setWebhook);
        log.info("registerBot called with webhookUrl: {}", webhookUrl);
        return botsApi;
    }
}