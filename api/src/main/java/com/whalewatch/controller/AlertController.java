package com.whalewatch.controller;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.common.dto.AlertSettingsDto;
import com.whalewatch.mapper.AlertSettingsMapper;
import com.whalewatch.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;
    private final AlertSettingsMapper alertSettingsMapper;

    public AlertController(AlertService alertService, AlertSettingsMapper alertSettingsMapper) {
        this.alertService = alertService;
        this.alertSettingsMapper = alertSettingsMapper;
    }

    @GetMapping()
    public List<AlertSettingsDto> getAlert() {
        return alertService.getAllAlerts().stream()
                .map(alertSettingsMapper::toDto)
                .collect(Collectors.toList());
    }

    //알림 생성
    @PutMapping()
    public AlertSettingsDto createAlert(@RequestBody AlertSettingsDto settings){
        AlertSetting entity = alertSettingsMapper.toEntity(settings); //Dto -> entity
        AlertSetting saved = alertService.createAlert(entity);
        return alertSettingsMapper.toDto(saved); //entity -> dto
    }

    @PostMapping("/{id}")
    public AlertSettingsDto updateAlert(@PathVariable int id,@RequestBody AlertSettingsDto settings){
        AlertSetting entity = alertSettingsMapper.toEntity(settings);
        AlertSetting updated = alertService.updateAlert(id,entity);
        return alertSettingsMapper.toDto(updated);
    }
}
