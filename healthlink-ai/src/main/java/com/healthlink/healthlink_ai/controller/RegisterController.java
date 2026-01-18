package com.healthlink.healthlink_ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.healthlink.healthlink_ai.repository.UserRepository;
import com.healthlink.healthlink_ai.entity.User;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // password encode ke liye

    public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {

        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/register";
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user to DB
        userRepository.save(user);

        // Redirect to login page with success message
        redirectAttributes.addAttribute("success", "true");
        return "redirect:/login";
    }
}