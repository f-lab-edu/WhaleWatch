package com.whalewatch.service;

import com.whalewatch.domain.User;
import com.whalewatch.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser() {
        // given
        User input = new User("test@test.com", "tester", "1234");
        User saved = new User("test@test.com", "tester", "1234");

        given(userRepository.save(input)).willReturn(saved);

        // when
        User result = userService.registerUser(input);

        // then
        assertNotNull(result); // 반환값이 null이 아님을 확인
        assertEquals("test@test.com", result.getEmail()); // 이메일 검증
        assertEquals("tester", result.getUsername()); // 이름 검증
        assertEquals("1234", result.getPassword()); // 비밀번호 검증
    }

    @Test
    void getUserInfo() {
        // given
        int userId = 1;
        User user = new User("info@test.com", "infotester", "1234");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        User result = userService.getUserInfo(userId);

        // then
        assertNotNull(result); // 반환값이 null이 아님을 확인
        assertEquals("info@test.com", result.getEmail()); // 이메일 검증
        assertEquals("infotester", result.getUsername()); // 이름 검증
        assertEquals("1234", result.getPassword()); // 비밀번호 검증
    }


}
