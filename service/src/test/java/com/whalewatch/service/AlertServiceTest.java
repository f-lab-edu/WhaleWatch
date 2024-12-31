package com.whalewatch.service;

import com.whalewatch.domain.AlertSetting;
import com.whalewatch.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    private AlertSetting alert1;
    private AlertSetting alert2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        alert1 = new AlertSetting();
        alert1.setId(1);
        alert1.setCoin("BTC");
        alert1.setThreshold(20000);
        alert1.setNotifyByEmail(true);

        alert2 = new AlertSetting();
        alert2.setId(2);
        alert2.setCoin("ETH");
        alert2.setThreshold(15000);
        alert2.setNotifyByEmail(false);
    }

    @Test
    public void testGetAllAlerts() {
        // Arrange
        when(alertRepository.findAll()).thenReturn(Arrays.asList(alert1, alert2));

        // Act
        List<AlertSetting> alerts = alertService.getAllAlerts();

        // Assert
        assertNotNull(alerts);
        assertEquals(2, alerts.size());
        verify(alertRepository, times(1)).findAll();
    }

    @Test
    public void testCreateAlert() {
        // Arrange
        when(alertRepository.save(any(AlertSetting.class))).thenReturn(alert1);

        // Act
        AlertSetting createdAlert = alertService.createAlert(alert1);

        // Assert
        assertNotNull(createdAlert);
        assertEquals("BTC", createdAlert.getCoin());
        verify(alertRepository, times(1)).save(alert1);
    }

    @Test
    public void testUpdateAlert_Success() {
        // Arrange
        AlertSetting updatedAlert = new AlertSetting();
        updatedAlert.setCoin("BTC");
        updatedAlert.setThreshold(25000);
        updatedAlert.setNotifyByEmail(false);

        when(alertRepository.findById(1)).thenReturn(Optional.of(alert1));
        when(alertRepository.save(any(AlertSetting.class))).thenReturn(updatedAlert);

        // Act
        AlertSetting result = alertService.updateAlert(1, updatedAlert);

        // Assert
        assertNotNull(result);
        assertEquals(25000, result.getThreshold());
        assertFalse(result.isNotifyByEmail());
        verify(alertRepository, times(1)).findById(1);
        verify(alertRepository, times(1)).save(alert1);
    }

    @Test
    public void testUpdateAlert_NotFound() {
        // Arrange
        AlertSetting updatedAlert = new AlertSetting();
        when(alertRepository.findById(3)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alertService.updateAlert(3, updatedAlert);
        });
        assertEquals("Not found", exception.getMessage());
        verify(alertRepository, times(1)).findById(3);
        verify(alertRepository, times(0)).save(any(AlertSetting.class));
    }
}
