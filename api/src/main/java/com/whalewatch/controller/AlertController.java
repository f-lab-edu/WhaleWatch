package com.whalewatch.controller;

import com.whalewatch.common.dto.AlertSettingsDto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    //알림 리스트 생성
    private List<AlertSettingsDto> alertsettings = new ArrayList<>();

    public AlertController(){
        //초기 더미데이터
        alertsettings.add(new AlertSettingsDto("BTC",10000,true));
        alertsettings.add(new AlertSettingsDto("ETH",5000,false));
    }

    @GetMapping("/settings")
    public List<AlertSettingsDto> getAlertSettings() {
        return alertsettings;
    }

    @PutMapping("/settings")
    public AlertSettingsDto updateAlertSetings(@RequestBody AlertSettingsDto settings){
        alertsettings.add(settings);
        return settings;
    }
}
