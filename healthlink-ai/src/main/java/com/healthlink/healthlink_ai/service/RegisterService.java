package com.healthlink.healthlink_ai.service;

import com.healthlink.healthlink_ai.entity.User;
import com.healthlink.healthlink_ai.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final UserRepository userRepository;

    public RegisterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        // Optional: password encode
        return userRepository.save(user);
    }
}