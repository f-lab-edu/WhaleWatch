package com.whalewatch.service;

import com.whalewatch.TradeDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FilteringService {

    private final Map<String, Double> volumeThresholdMap = new ConcurrentHashMap<>();

    public FilteringService() {
        // 테스트용 초기값 설정
        volumeThresholdMap.put("KRW-BTC", 0.5);
        volumeThresholdMap.put("KRW-ETH", 0.5);
    }

    public double getVolumeThreshold(String coin) {
        return volumeThresholdMap.get(coin);
    }

    public boolean shouldAlert(TradeDto dto) {
        if (dto.getCode() == null || dto.getTradeVolume() == null) {
            return false;
        }
        double threshold = getVolumeThreshold(dto.getCode());
        return dto.getTradeVolume() >= threshold;
    }


}
