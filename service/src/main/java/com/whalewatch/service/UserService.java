package com.whalewatch.service;

import com.whalewatch.domain.User;
import com.whalewatch.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User loginUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserInfo(int id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }
}
