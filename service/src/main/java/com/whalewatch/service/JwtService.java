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

    /**
     * 로그인 -> 비밀번호 검증 -> Access/Refresh 토큰 발급
     */
    public TokenResponseDto login(String email, String rawPassword) {
        // 1) 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // 2) 비밀번호 일치 여부 (BCrypt 매치)
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 3) 토큰 생성
        String accessToken = tokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        // 4) 기존 refreshToken 있으면 제거 (단일 로그인 정책 등)
        jwtTokenRepository.deleteByEmail(user.getEmail());

        // 5) 새 refreshToken 저장
        JwtToken refreshTokenEntity = new JwtToken(
                refreshToken,
                user.getEmail(),
                LocalDateTime.now().plusSeconds(1209600) // 2주 예시
        );
        jwtTokenRepository.save(refreshTokenEntity);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    /**
     * Refresh Token -> 새 Access Token 발급
     */
    public TokenResponseDto refreshAccessToken(String refreshToken) {
        // 1) DB에서 해당 refreshToken 조회
        JwtToken stored = jwtTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // 2) 만료 시간 확인
        if (stored.getExpiry().isBefore(LocalDateTime.now())) {
            // 만료된 토큰 -> DB에서 삭제 후 에러
            jwtTokenRepository.delete(stored);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }

        // 3) RefreshToken 자체가 위조/유효한지(서명) 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            jwtTokenRepository.delete(stored);
            throw new RuntimeException("Invalid refresh token signature. Please login again.");
        }

        // 4) 토큰에서 email 추출 -> 새 Access Token 발급
        String email = tokenProvider.getEmailFromToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessToken(email);

        // (정책에 따라 RefreshToken도 재발급할 수 있음. 여기서는 재사용)
        return new TokenResponseDto(newAccessToken, refreshToken);
    }
}
