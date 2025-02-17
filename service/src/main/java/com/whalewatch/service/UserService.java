package com.whalewatch.service;

import com.whalewatch.domain.User;
import com.whalewatch.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TelegramUserBot telegramUserBot;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       TelegramUserBot telegramUserBot) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.telegramUserBot = telegramUserBot;
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User getUserInfo(int id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    // 이메일을 받아 새로운 OTP 생성 후, 해당 사용자의 otpHash를 업데이트하고 텔레그램으로 전송
    public void requestLoginOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // OTP 생성
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
        String otpHash = passwordEncoder.encode(otp);
        user.setOtpHash(otpHash);
        userRepository.save(user);

        // 사용자의 telegramChatId가 존재하면 텔레그램으로 OTP 전송
        if (user.getTelegramChatId() != null) {
            telegramUserBot.sendTextMessage(user.getTelegramChatId(), "Your login OTP: " + otp);
        } else {
            throw new RuntimeException("User is not registered with Telegram");
        }
    }

    // 입력받은 OTP가 저장된 otpHash와 일치하면 로그인 성공 처리
    public User loginWithOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOtpHash() != null && passwordEncoder.matches(otp, user.getOtpHash())) {
            // OTP는 한 번 사용 후 삭제
            user.setOtpHash(null);
            userRepository.save(user);
            return user;
        } else {
            throw new RuntimeException("Invalid OTP");
        }
    }
}
