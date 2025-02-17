package com.whalewatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProducerProperties {

    private String bootstrapServers;
    private ProducerProperties producer = new ProducerProperties();

    public String getBootstrapServers() {
        return bootstrapServers;
    }
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
    public ProducerProperties getProducer() {
        return producer;
    }
    public void setProducer(ProducerProperties producer) {
        this.producer = producer;
    }

    public static class ProducerProperties {
        private String keySerializer;
        private String valueSerializer;

        public String getKeySerializer() {
            return keySerializer;
        }
        public void setKeySerializer(String keySerializer) {
            this.keySerializer = keySerializer;
        }
        public String getValueSerializer() {
            return valueSerializer;
        }
        public void setValueSerializer(String valueSerializer) {
            this.valueSerializer = valueSerializer;
        }
    }
}
