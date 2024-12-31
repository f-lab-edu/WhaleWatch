package com.whalewatch.service;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.repository.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void getAllAlerts() {
        // given
        AlertSetting a1 = new AlertSetting("BTC", 30000, true);
        AlertSetting a2 = new AlertSetting("ETH", 2000, false);

        given(alertRepository.findAll()).willReturn(Arrays.asList(a1, a2));

        // when
        List<AlertSetting> alerts = alertService.getAllAlerts();

        // then
        assertEquals(2, alerts.size()); // 리스트 크기 검증

        // 첫 번째 객체 검증
        assertEquals("BTC", alerts.get(0).getCoin());
        assertEquals(30000, alerts.get(0).getThreshold());
        assertTrue(alerts.get(0).isNotifyByEmail());

        // 두 번째 객체 검증
        assertEquals("ETH", alerts.get(1).getCoin());
        assertEquals(2000, alerts.get(1).getThreshold());
        assertFalse(alerts.get(1).isNotifyByEmail());
    }

    @Test
    void createAlert() {
        // given
        AlertSetting input = new AlertSetting("BTC", 30000, true);
        AlertSetting saved = new AlertSetting("BTC", 30000, true);

        given(alertRepository.save(input)).willReturn(saved);

        // when
        AlertSetting result = alertService.createAlert(input);

        // then
        assertNotNull(result);
        assertEquals("BTC", result.getCoin());
        assertEquals(30000, result.getThreshold());
        assertTrue(result.isNotifyByEmail());
    }

    @Test
    void updateAlert() {
        // given
        int alertId = 1;
        AlertSetting existing = new AlertSetting("BTC", 10000, false);
        given(alertRepository.findById(alertId)).willReturn(Optional.of(existing));

        AlertSetting updated = new AlertSetting("ETH", 2000, true);
        given(alertRepository.save(existing)).willAnswer(invocation -> {
            AlertSetting toUpdate = invocation.getArgument(0);
            toUpdate.setCoin(updated.getCoin());
            toUpdate.setThreshold(updated.getThreshold());
            toUpdate.setNotifyByEmail(updated.isNotifyByEmail());
            return toUpdate;
        });

        // when
        AlertSetting result = alertService.updateAlert(alertId, updated);

        // then
        assertEquals("ETH", result.getCoin()); // 업데이트된 코인 이름 검증
        assertEquals(2000, result.getThreshold()); // 업데이트된 임계값 검증
        assertTrue(result.isNotifyByEmail()); // 업데이트된 이메일 알림 여부 검증
    }

}
