package com.whalewatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whalewatch.domain.AlertSetting;
import com.whalewatch.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        alertRepository.deleteAll();

        alertRepository.save(new AlertSetting("BTC", 30000, true));
    }

    @Test
    @DisplayName("GET /api/alerts")
    void testGetAlerts() throws Exception {
        // given

        // when & then
        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].coin", is("BTC")))
                .andExpect(jsonPath("$[0].threshold", is(30000)))
                .andExpect(jsonPath("$[0].notifyByEmail", is(true)));
    }

    @Test
    @DisplayName("PUT /api/alerts ")
    void testCreateAlert() throws Exception {
        // given
        AlertSetting request = new AlertSetting("ETH", 2000, true);

        // when
        mockMvc.perform(put("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coin").value("ETH"))
                .andExpect(jsonPath("$.threshold").value(2000))
                .andExpect(jsonPath("$.notifyByEmail").value(true));

        // then - DB 확인
        List<AlertSetting> all = alertRepository.findAll();
        assertEquals(2, all.size());
        AlertSetting savedAlert = all.get(1); // 저장된 객체 가져오기
        assertEquals("ETH", savedAlert.getCoin()); // coin 값 검증
        assertEquals(2000, savedAlert.getThreshold()); // threshold 값 검증
        assertEquals(true, savedAlert.isNotifyByEmail()); // notifyByEmail 값 검증
    }

    @Test
    @DisplayName("POST /api/alerts/{id}")
    void testUpdateAlert() throws Exception {
        // given

        AlertSetting first = alertRepository.findAll().get(0); //BTC
        AlertSetting request = new AlertSetting("DOGE", 1000, true);

        // when & then
        mockMvc.perform(post("/api/alerts/" + first.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coin").value("DOGE"))
                .andExpect(jsonPath("$.threshold").value(1000))
                .andExpect(jsonPath("$.notifyByEmail").value(true));
    }
}
