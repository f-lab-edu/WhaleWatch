package com.whalewatch.controller;

import com.whalewatch.domain.User;
import com.whalewatch.dto.TokenResponseDto;
import com.whalewatch.dto.UserDto;
import com.whalewatch.mapper.UserMapper;
import com.whalewatch.service.JwtService;
import com.whalewatch.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public UserController(UserService userService,
                          UserMapper userMapper,
                          JwtService jwtService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }


    @PostMapping("/login")
    public TokenResponseDto loginUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        return jwtService.login(email, otp);
    }

    // 사용자의 이메일을 받아 OTP를 생성 후 텔레그램으로 전송
    @PostMapping("/request-otp")
    public String requestOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        userService.requestLoginOtp(email);
        return "OTP has been sent to Telegram.";
    }

    @PostMapping("/refresh")
    public TokenResponseDto refreshToken(@RequestBody TokenResponseDto tokenDto){
        return jwtService.refreshAccessToken(tokenDto.getRefreshToken());
    }

    @GetMapping("/info/{id}")
    public UserDto getUserInfo(@PathVariable int id) {
        User user = userService.getUserInfo(id);
        return userMapper.toDto(user);
    }
}