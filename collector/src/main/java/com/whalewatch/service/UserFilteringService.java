package com.whalewatch.service;

import com.whalewatch.TradeDto;
import com.whalewatch.domain.AlertSetting;
import com.whalewatch.domain.UserAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFilteringService {

    private static final Logger log = LoggerFactory.getLogger(UserFilteringService.class);

    private final AlertService alertService;
    private final UserAlertService userAlertService;

    public UserFilteringService(AlertService alertService,
                                UserAlertService userAlertService) {
        this.alertService = alertService;
        this.userAlertService = userAlertService;
    }

    public void userFiltering(TradeDto dto) {
        if (dto.getCode() == null || dto.getTradeVolume() == null) {
            return;
        }

        List<AlertSetting> settings = alertService.getAllAlerts();
        for (AlertSetting setting : settings) {
            if (!setting.getCoin().equalsIgnoreCase(dto.getCode())) {
                continue;
            }
            double threshold = setting.getThreshold();
            if (dto.getTradeVolume() >= threshold) {
                log.info("[USER] coin={}, volume={} >= threshold({}) => user_alert Insert",
                        dto.getCode(), dto.getTradeVolume(), threshold);

                // 알림 이력 저장
                int userId = setting.getUserId();
                UserAlert alert = new UserAlert(
                        userId,
                        dto.getCode(),
                        dto.getTradePrice(),
                        dto.getTradeVolume(),
                        dto.getTradeTimestamp()
                );
                userAlertService.createUserAlert(alert);

            }
        }
    }
}
