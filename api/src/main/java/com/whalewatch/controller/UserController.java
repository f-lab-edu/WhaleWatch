package com.whalewatch.controller;

import com.whalewatch.domain.User;
import com.whalewatch.common.dto.UserDto;
import com.whalewatch.mapper.UserMapper;
import com.whalewatch.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService,UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserDto registerUser(@RequestBody UserDto userDto) {
        User entity = userMapper.toEntity(userDto);
        User saved = userService.registerUser(entity);
        return userMapper.toDto(saved);
    }

    @PostMapping("/login")
    public UserDto loginUser(@RequestBody UserDto userDto) {
        User user = userService.loginUser(userDto.getEmail());

        if (!user.getPassword().equals(userDto.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        return userMapper.toDto(user);
    }

    @GetMapping("{id}")
    public UserDto getUserInfo(@PathVariable int id) {
        User user = userService.getUserInfo(id);
        return userMapper.toDto(user);
    }
}
