package com.whalewatch.controller;

import com.whalewatch.common.dto.AlertSettingsDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @GetMapping("/settings")
    public AlertSettingsDto getAlertSettings() {
        return new AlertSettingsDto("BTC", 10000, true);
    }

    @PutMapping("/settings")
    public String updateAlertSetings(@RequestBody AlertSettingsDto settings){
        return "updated setting";
    }
}
