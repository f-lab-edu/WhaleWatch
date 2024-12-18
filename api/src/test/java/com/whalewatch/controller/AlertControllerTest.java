package com.whalewatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whalewatch.common.dto.AlertSettingsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AlertController.class)
public class AlertControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAlertSettings() throws Exception {
        mockMvc.perform(get("/api/alerts/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coin").value("BTC"))
                .andExpect(jsonPath("$.notifyByEmail").value(true));
    }

    @Test
    void updateAlertSettings() throws Exception {
        AlertSettingsDto dto = new AlertSettingsDto("ETH", 20000, false);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/alerts/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("updated setting"));
    }
}
