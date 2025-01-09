package com.whalewatch.controller;

import com.whalewatch.domain.User;
import com.whalewatch.dto.TokenResponseDto;
import com.whalewatch.dto.UserDto;
import com.whalewatch.mapper.UserMapper;
import com.whalewatch.service.JwtService;
import com.whalewatch.service.UserService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public UserDto registerUser(@RequestBody UserDto userDto) {
        User entity = userMapper.toEntity(userDto);
        User saved = userService.registerUser(entity);
        return userMapper.toDto(saved);
    }

    @PostMapping("/login")
    public TokenResponseDto loginUser(@RequestBody UserDto userDto) {
        // JwtService로 로그인 + 토큰 발급
        return jwtService.login(userDto.getEmail(),userDto.getPassword());
    }

    @PostMapping("/refresh")
    public TokenResponseDto refreshToken(@RequestBody TokenResponseDto tokenDto){
        return jwtService.refreshAccessToken(tokenDto.getRefreshToken());
    }

    @GetMapping("{id}")
    public UserDto getUserInfo(@PathVariable int id) {
        User user = userService.getUserInfo(id);
        return userMapper.toDto(user);
    }
}