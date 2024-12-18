package com.whalewatch.controller;

import com.whalewatch.domain.User;
import com.whalewatch.dto.UserDto;
import com.whalewatch.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto registerUser(@RequestBody UserDto userDto) {
        User newUser = new User(userDto.getEmail(), userDto.getUsername());
        User saved = userService.registerUser(newUser);
        return new UserDto(saved.getId(), saved.getEmail(), saved.getUsername());
    }

    @PostMapping("/login")
    public UserDto loginUser(@RequestBody UserDto userDto) {
        User user = userService.loginUser(userDto.getEmail());
        return new UserDto(user.getId(), user.getEmail(), user.getUsername());
    }

    @GetMapping("{id}")
    public UserDto getUserInfo(@PathVariable int id) {
        User user = userService.getUserInfo(id);
        return new UserDto(user.getId(), user.getEmail(), user.getUsername());
    }
}
