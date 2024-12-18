package com.whalewatch.controller;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.dto.AlertSettingsDto;
import com.whalewatch.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping()
    public List<AlertSettingsDto> getAlert() {
        List<AlertSetting> alerts = alertService.getAllAlerts();
        return alerts.stream()
                .map(a -> new AlertSettingsDto(a.getId(),a.getCoin(),a.getThreshold(),a.isNotifyByEmail()))
                .collect(Collectors.toList());
    }

    //알림 생성
    @PutMapping()
    public AlertSettingsDto createAlert(@PathVariable int id, @RequestBody AlertSettingsDto settings){
        AlertSetting newAlert = new AlertSetting(settings.getCoin(), settings.getThreshold(), settings.isNotifyByEmail());
        AlertSetting saved = alertService.createAlert(newAlert);

        return new AlertSettingsDto(saved.getId(), saved.getCoin(), saved.getThreshold(), saved.isNotifyByEmail());
    }

    @PostMapping("/{id}")
    public AlertSettingsDto updateAlert(@PathVariable int id,@RequestBody AlertSettingsDto settings){
        AlertSetting toUpdate = new AlertSetting(settings.getCoin(), settings.getThreshold(), settings.isNotifyByEmail());
        AlertSetting updated = alertService.updateAlert(id, toUpdate);
        return new AlertSettingsDto(updated.getId(), updated.getCoin(), updated.getThreshold(), updated.isNotifyByEmail());
    }
}
