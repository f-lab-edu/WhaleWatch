package com.whalewatch.controller;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.dto.AlertSettingsDto;
import com.whalewatch.dto.UserAlertDto;
import com.whalewatch.mapper.AlertSettingsMapper;
import com.whalewatch.mapper.UserAlertMapper;
import com.whalewatch.service.AlertService;
import com.whalewatch.service.UserAlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;
    private final AlertSettingsMapper alertSettingsMapper;

    private final UserAlertService userAlertService;
    private final UserAlertMapper userAlertMapper;


    public AlertController(AlertService alertService,
                           AlertSettingsMapper alertSettingsMapper,
                           UserAlertService userAlertService,
                           UserAlertMapper userAlertMapper) {
        this.alertService = alertService;
        this.alertSettingsMapper = alertSettingsMapper;
        this.userAlertService = userAlertService;
        this.userAlertMapper = userAlertMapper;
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


    //user가 설정한 임계값을 넘는 코인 조회

    @GetMapping("/history")
    public List<UserAlertDto> getAllUserAlerts() {
        return userAlertService.getAllAlerts().stream()
                .map(userAlertMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/history/{userId}")
    public List<UserAlertDto> getUserAlerts(@PathVariable Integer userId) {
        return userAlertService.getAlertsByUserId(userId).stream()
                .map(userAlertMapper::toDto)
                .collect(Collectors.toList());
    }
}
