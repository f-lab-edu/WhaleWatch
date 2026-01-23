package com.whalewatch.config;


import com.whalewatch.dto.TransactionEventDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);
    
    private final KafkaProducerProperties kafkaProducerProperties;

    public KafkaConsumerConfig(KafkaProducerProperties kafkaProducerProperties) {
        this.kafkaProducerProperties = kafkaProducerProperties;
    }

    @Bean
    public ConsumerFactory<String, TransactionEventDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "whalewatch_group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // 동시성 및 성능 최적화 설정
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // 한 번에 가져올 최대 레코드 수
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 폴링 간격 최대 5분
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); // 세션 타임아웃 30초
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000); // 하트비트 10초
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 오프셋 초기화 전략
        
        JsonDeserializer<TransactionEventDto> deserializer = new JsonDeserializer<>(TransactionEventDto.class);
        deserializer.addTrustedPackages("com.whalewatch.dto");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * DLQ(Dead Letter Queue) 전송을 위한 Recoverer
     * 최대 재시도 후 실패한 메시지를 원본 토픽명 + ".dlq" 토픽으로 전송
     */
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (record, ex) -> {
            String dlqTopic = record.topic() + ".dlq";
            log.error("[DLQ] Sending failed message to {}: key={}, error={}", 
                    dlqTopic, record.key(), ex.getMessage());
            return new org.apache.kafka.common.TopicPartition(dlqTopic, record.partition());
        });
    }

    /**
     * Exponential Backoff 재시도 전략이 적용된 에러 핸들러
     * - 초기 간격: 1초
     * - 최대 간격: 10초
     * - 배수: 2
     * - 최대 재시도: 3회
     */
    @Bean
    public CommonErrorHandler commonErrorHandler(DeadLetterPublishingRecoverer recoverer) {
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(1000L); // 1초
        backOff.setMaxInterval(10000L); // 최대 10초
        backOff.setMultiplier(2.0); // 2배씩 증가
        backOff.setMaxElapsedTime(30000L); // 최대 30초

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        
        // 재시도 불필요한 예외 타입 지정 (바로 DLQ로 전송)
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                NullPointerException.class
        );
        
        // 에러 발생 시 로깅
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("[Retry] Attempt {} for topic={}, partition={}, offset={}, error={}",
                    deliveryAttempt, record.topic(), record.partition(), record.offset(), ex.getMessage());
        });
        
        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionEventDto> kafkaListenerContainerFactory(
            CommonErrorHandler commonErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(commonErrorHandler);
        
        // 동시성 설정 - 3개 스레드로 병렬 처리
        factory.setConcurrency(3);
        
        log.info("[KafkaConsumerConfig] Configured with concurrency=3, exponential backoff retry, DLQ enabled");
        
        return factory;
    }
}