package com.whalewatch.service;

import com.whalewatch.domain.User;
import com.whalewatch.dto.UserOtpEventDto;
import com.whalewatch.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User getUserInfo(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    // Telegram 채팅 ID로 사용자 조회
    public User findByTelegramChatId(Long chatId) {
        return userRepository.findByTelegramChatId(chatId)
                .orElseThrow(() -> new RuntimeException("User not found with chatId: " + chatId));
    }

    // 이메일을 받아 OTP 생성 후, 해당 사용자의 otpHash 업데이트 및 텔레그램 메시지 전송 이벤트 발행
    public void requestLoginOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
        String otpHash = passwordEncoder.encode(otp);
        user.setOtpHash(otpHash);
        userRepository.save(user);

        if (user.getTelegramChatId() != null) {
            UserOtpEventDto event = new UserOtpEventDto(user.getTelegramChatId(), "Your login OTP: " + otp);
            kafkaTemplate.send("user_otp_topic", event);
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
