package com.whalewatch.service;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;

        //더미데이터
        alertRepository.save(new AlertSetting("BTC", 10000, true));
        alertRepository.save(new AlertSetting("ETH", 15000, false));
    }

    public List<AlertSetting> getAllAlerts(){
        return alertRepository.findAll();
    }

    public AlertSetting createAlert(AlertSetting alert) {
        return alertRepository.save(alert);
    }

    public AlertSetting updateAlert(int id, AlertSetting updatedAlert) {
        AlertSetting alert = alertRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        alert.setCoin(updatedAlert.getCoin());
        alert.setThreshold(updatedAlert.getThreshold());
        alert.setNotifyByEmail(updatedAlert.isNotifyByEmail());
        return alertRepository.save(alert);
    }


}
