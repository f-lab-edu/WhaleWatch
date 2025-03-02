package com.whalewatch.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisStateService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final long DEFAULT_TTL = 5;

    public RedisStateService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 회원가입 상태 저장
    public void saveRegistrationData(Long chatId, RegistrationData data) {
        redisTemplate.opsForValue().set("registration:" + chatId, data, DEFAULT_TTL, TimeUnit.MINUTES);
    }

    public RegistrationData getRegistrationData(Long chatId) {
        return (RegistrationData) redisTemplate.opsForValue().get("registration:" + chatId);
    }

    public void deleteRegistrationData(Long chatId) {
        redisTemplate.delete("registration:" + chatId);
    }

    // 임계값 설정 상태 저장
    public void saveThresholdData(Long chatId, ThresholdSettingData data) {
        redisTemplate.opsForValue().set("threshold:" + chatId, data, DEFAULT_TTL, TimeUnit.MINUTES);
    }

    public ThresholdSettingData getThresholdData(Long chatId) {
        return (ThresholdSettingData) redisTemplate.opsForValue().get("threshold:" + chatId);
    }

    public void deleteThresholdData(Long chatId) {
        redisTemplate.delete("threshold:" + chatId);
    }
}
