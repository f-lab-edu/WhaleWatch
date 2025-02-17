package com.whalewatch;

import com.whalewatch.service.WebSocketManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WhaleWatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(WhaleWatchApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(WebSocketManager webSocketManager) {
        return args -> {
            webSocketManager.startAll();
        };
    }

}
