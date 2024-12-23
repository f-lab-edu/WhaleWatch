package com.whalewatch.service;


import com.whalewatch.WhaleWatchApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = WhaleWatchApplication.class)
public class AlertServiceTest {

    @Autowired
    AlertService alertService;

    @Test
    void testGetAllAlertsHasDummyData() {
        var alerts = alertService.getAllAlerts();
        Assertions.assertFalse(alerts.isEmpty());
        Assertions.assertTrue(alerts.stream().anyMatch(a -> a.getCoin().equals("BTC") && a.getThreshold() == 10000));
        Assertions.assertTrue(alerts.stream().anyMatch(a -> a.getCoin().equals("ETH") && a.getThreshold() == 15000));
    }
}