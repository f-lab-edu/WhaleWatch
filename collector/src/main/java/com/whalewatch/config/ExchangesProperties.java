package com.whalewatch.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "exchanges")
public class ExchangesProperties {
    private Map<String, ExchangeConfig> exchanges;

    public Map<String, ExchangeConfig> getExchanges() {
        return exchanges;
    }
    public void setExchanges(Map<String, ExchangeConfig> exchanges) {
        this.exchanges = exchanges;
    }

    public static class ExchangeConfig {
        private boolean enabled;
        private String websocketType;
        private String url;
        private Map<String, String> mapping;
        private Map<String, Double> threshold;

        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        public String getWebsocketType() {
            return websocketType;
        }
        public void setWebsocketType(String websocketType) {
            this.websocketType = websocketType;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public Map<String, String> getMapping() {
            return mapping;
        }
        public void setMapping(Map<String, String> mapping) {
            this.mapping = mapping;
        }
        public Map<String, Double> getThreshold() {
            return threshold;
        }
        public void setThreshold(Map<String, Double> threshold) {
            this.threshold = threshold;
        }
    }
}
