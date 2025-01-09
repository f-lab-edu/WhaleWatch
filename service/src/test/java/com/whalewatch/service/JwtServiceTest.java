package com.whalewatch.service;

import com.whalewatch.config.JwtTokenProvider;
import com.whalewatch.domain.JwtToken;
import com.whalewatch.domain.User;
import com.whalewatch.dto.TokenResponseDto;
import com.whalewatch.repository.JwtTokenRepository;
import com.whalewatch.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class JwtServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenRepository refreshTokenRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void loginSuccess() {
        // given
        String email = "test@test.com";
        String Password = "test";
        String encodedPassword = "$2a$10$ABCD123..."; // bcrypt 해싱된 값 가정
        User user = new User(email, "logtester", encodedPassword);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        // 비밀번호 매칭
        given(passwordEncoder.matches(Password, encodedPassword)).willReturn(true);

        // JWT 생성
        given(jwtTokenProvider.generateAccessToken(email)).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken(email)).willReturn("refresh-token");

        // when
        TokenResponseDto result = jwtService.login(email, Password);

        // then
        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        verify(refreshTokenRepository).deleteByEmail(email);
        verify(refreshTokenRepository).save(any(JwtToken.class));
    }

    @Test
    void refreshAccessTokenSuccess() {
        // given
        String refreshToken = "valid-refresh-token";
        JwtToken stored = new JwtToken(refreshToken, "refresh@test.com", LocalDateTime.now().plusDays(1));
        given(refreshTokenRepository.findByToken(refreshToken)).willReturn(Optional.of(stored));

        // 토큰 서명/만료 검증
        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);

        // 토큰에서 이메일 추출
        given(jwtTokenProvider.getEmailFromToken(refreshToken)).willReturn("refresh@test.com");

        // 새 Access Token 발급
        given(jwtTokenProvider.generateAccessToken("refresh@test.com")).willReturn("new-access-token");

        // when
        TokenResponseDto result = jwtService.refreshAccessToken(refreshToken);

        // then
        assertNotNull(result);
        assertEquals("new-access-token", result.getAccessToken());
        assertEquals("valid-refresh-token", result.getRefreshToken());
    }
}
