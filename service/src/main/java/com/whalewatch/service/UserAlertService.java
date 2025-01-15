package com.whalewatch.service;

import com.whalewatch.domain.UserAlert;
import com.whalewatch.repository.UserAlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAlertService {
    private final UserAlertRepository userAlertRepository;

    public UserAlertService(UserAlertRepository userAlertRepository) {
        this.userAlertRepository = userAlertRepository;
    }

    public UserAlert createUserAlert(UserAlert alert) {
        return userAlertRepository.save(alert);
    }

    public List<UserAlert> getAllAlerts() {
        return userAlertRepository.findAll();
    }

    public List<UserAlert> getAlertsByUserId(Integer userId) {
        return userAlertRepository.findByUserId(userId);
    }
}
