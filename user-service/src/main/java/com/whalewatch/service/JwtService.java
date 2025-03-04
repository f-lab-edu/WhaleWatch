package com.whalewatch.service;

import com.whalewatch.config.JwtTokenProvider;
import com.whalewatch.domain.JwtToken;
import com.whalewatch.domain.User;
import com.whalewatch.dto.TokenResponseDto;
import com.whalewatch.repository.JwtTokenRepository;
import com.whalewatch.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JwtService {
    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public JwtService(UserRepository userRepository,
                      JwtTokenRepository jwtTokenRepository,
                      JwtTokenProvider tokenProvider,
                      PasswordEncoder passwordEncoder,
                      UserService userService) {
        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public TokenResponseDto login(String email, String otp) {
        User user = userService.loginWithOtp(email, otp);
        String accessToken = tokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());
        return new TokenResponseDto(accessToken, refreshToken);
    }

    public TokenResponseDto refreshAccessToken(String refreshToken) {
        // RefreshToken 유효성 검사
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token.");
        }
        String email = tokenProvider.getEmailFromToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessToken(email);
        return new TokenResponseDto(newAccessToken, refreshToken);
    }
}
