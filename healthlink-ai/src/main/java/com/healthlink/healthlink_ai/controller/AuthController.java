package com.healthlink.healthlink_ai.controller;

import com.healthlink.healthlink_ai.entity.User;
import com.healthlink.healthlink_ai.repository.UserRepository;
import com.healthlink.healthlink_ai.security.JwtUtil;
import jakarta.servlet.http.Cookie; // Required import
import jakarta.servlet.http.HttpServletResponse; // Required import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // HttpServletResponse ko constructor mein ya method parameter mein inject karein
    public AuthController(UserRepository userRepository,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse response) { // 👈🏻 HttpServletResponse add kiya

        User user = userRepository.findByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return "redirect:/login?error=true";
        }

        // 1. JWT generate karo
        String token = jwtUtil.generateToken(user.getUsername());
        

        // 2. Token ko HttpOnly Cookie mein set karo
        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setHttpOnly(true); // JavaScript access nahi kar sakta (Security)
        cookie.setSecure(false); // Agar aap HTTPS use nahi kar rahe hain to false rakhein
        cookie.setMaxAge(3600); // 1 ghanta
        cookie.setPath("/"); 
        cookie.setDomain("localhost");// Poore application ke liye available

        // Agar dono services alag ports par hain (jaise 8080 aur 7070), toh domain set karna mushkil ho sakta hai.
        // Agar same top-level domain hai (jaise healthlink.com aur dashboard.healthlink.com), toh cookie.setDomain(".healthlink.com") use hota.

        response.addCookie(cookie); // Response mein cookie add kiya

        // 3. Browser ko dashboard microservice ke URL par redirect karo (ab token URL mein nahi hai)
        //String token = jwtUtil.generateToken(user.getUsername());
        return "redirect:http://localhost:7070/dashboard?token=" + token;
       
     // Isse URL banega: /dashboard?token=eyJhbG... (Ab server /dashboard ko pehchan lega) // Query param hataya
    }
}