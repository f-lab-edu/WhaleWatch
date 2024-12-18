package com.whalewatch.controller;

import com.whalewatch.dto.AlertSettingsDto;
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
        alertsettings.add(new AlertSettingsDto(1,"BTC",10000,true));
        alertsettings.add(new AlertSettingsDto(2,"ETH",5000,false));
    }

    @GetMapping()
    public List<AlertSettingsDto> getAlert() {
        return alertsettings;
    }

    //알림 생성
    @PutMapping()
    public AlertSettingsDto createAlert(@PathVariable int id, @RequestBody AlertSettingsDto settings){
        alertsettings.add(settings);
        return settings;
    }

    @PostMapping("/{id}")
    public AlertSettingsDto updateAlert(@PathVariable int id,@RequestBody AlertSettingsDto settings){
        alertsettings.add(settings);
        return settings;
    }
}
