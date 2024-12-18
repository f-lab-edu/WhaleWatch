package com.whalewatch.controller;

import com.whalewatch.common.dto.UserDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public UserDto registerUser(@RequestBody UserDto userDto) {
        return userDto;
    }

    @PostMapping("/login")
    public UserDto loginUser(@RequestBody UserDto userDto) {
        return userDto;
    }

    @GetMapping("/me")
    public UserDto getUserInfo() {
        return new UserDto("test@naver.com", "Test");
    }
}
