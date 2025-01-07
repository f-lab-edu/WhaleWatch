package com.whalewatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whalewatch.domain.User;
import com.whalewatch.dto.TokenResponseDto;
import com.whalewatch.dto.UserDto;
import com.whalewatch.repository.JwtTokenRepository;
import com.whalewatch.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "jwt.secret-key=zTjEp7AUmDS+bUZKV5OFIVUtFL7EQCMflxiZ3gxpxo0=",
        "jwt.access-token-validity-in-seconds=600",
        "jwt.refresh-token-validity-in-seconds=1209600"
})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        jwtTokenRepository.deleteAll();
        userRepository.deleteAll();

        String hashed = passwordEncoder.encode("1234");
        userRepository.save(new User("test@test.com", "tester", hashed));
    }

    @Test
    void testRegisterUser() throws Exception {
        // given
        UserDto request = new UserDto();
        request.setEmail("test2@test.com");
        request.setUsername("tester2");
        request.setPassword("1234");

        // when
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("test2@test.com"))
                .andExpect(jsonPath("$.username").value("tester2"));

        List<User> all = userRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testLoginUser() throws Exception {
        // given
        UserDto request = new UserDto();
        request.setEmail("test@test.com");
        request.setPassword("1234");

        // when
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void testGetUserInfo() throws Exception {
        // given
        User user = userRepository.findAll().get(0);

        // 로그인
        UserDto loginRequest = new UserDto();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("1234");

        String loginResponse = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TokenResponseDto tokens = objectMapper.readValue(loginResponse, TokenResponseDto.class);
        String accessToken = tokens.getAccessToken();

        // when & then
        mockMvc.perform(get("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.username").value("tester"));
    }

    @Test
    void testRefreshToken() throws Exception {
        //given
        UserDto loginRequest = new UserDto();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("1234");

        //when
        String loginResponse = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TokenResponseDto tokens = objectMapper.readValue(loginResponse, TokenResponseDto.class);
        String refreshToken = tokens.getRefreshToken();

        //given - accesstoken 재발급
        TokenResponseDto refreshRequest = new TokenResponseDto("", refreshToken);

        // then
        mockMvc.perform(post("/api/users/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }
}
