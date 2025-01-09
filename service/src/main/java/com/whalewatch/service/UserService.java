package com.whalewatch.service;

import com.whalewatch.domain.User;
import com.whalewatch.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        String hashed = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashed);

        return userRepository.save(user);
    }

    public User getUserInfo(int id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }
}
