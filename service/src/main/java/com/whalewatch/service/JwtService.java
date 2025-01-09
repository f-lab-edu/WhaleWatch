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

    public JwtService(UserRepository userRepository,
                       JwtTokenRepository jwtTokenRepository,
                       JwtTokenProvider tokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponseDto login(String email, String Password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(Password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = tokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        //기존 refreshToken 있으면 제거
        jwtTokenRepository.deleteByEmail(user.getEmail());

        // refreshToken 저장
        JwtToken refreshTokenEntity = new JwtToken(
                refreshToken,
                user.getEmail(),
                LocalDateTime.now().plusSeconds(1209600)
        );
        jwtTokenRepository.save(refreshTokenEntity);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public TokenResponseDto refreshAccessToken(String refreshToken) {
        JwtToken stored = jwtTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (stored.getExpiry().isBefore(LocalDateTime.now())) {
            jwtTokenRepository.delete(stored);
            throw new RuntimeException("Token expired.");
        }

        // RefreshToken 자체가 유효한지
        if (!tokenProvider.validateToken(refreshToken)) {
            jwtTokenRepository.delete(stored);
            throw new RuntimeException("Invalid token signature.");
        }

        // 새 Access Token 발급
        String email = tokenProvider.getEmailFromToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessToken(email);

        return new TokenResponseDto(newAccessToken, refreshToken);
    }
}
