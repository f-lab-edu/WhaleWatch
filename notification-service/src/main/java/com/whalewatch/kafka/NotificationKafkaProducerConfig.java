package com.whalewatch.kafka;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.whalewatch.config.KafkaProducerProperties;
import com.whalewatch.dto.TelegramAlertMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class NotificationKafkaProducerConfig {

    private final KafkaProducerProperties kafkaProperties;

    public NotificationKafkaProducerConfig(KafkaProducerProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ProducerFactory<String, TelegramAlertMessage> telegramProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                kafkaProperties.getProducer().getKeySerializer() != null ?
                        kafkaProperties.getProducer().getKeySerializer() : StringSerializer.class.getName());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                kafkaProperties.getProducer().getValueSerializer() != null ?
                        kafkaProperties.getProducer().getValueSerializer() : JsonSerializer.class.getName());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, TelegramAlertMessage> telegramKafkaTemplate() {
        return new KafkaTemplate<>(telegramProducerFactory());
    }
}
