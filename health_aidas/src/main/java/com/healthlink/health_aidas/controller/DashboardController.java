package com.healthlink.health_aidas.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Value("${register.service.url:http://localhost:7071}")
    private String registerServiceUrl;

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return "dashboard";
                }
            }
        }
        // Redirect to login service
        return "redirect:" + registerServiceUrl + "/login";
    }
}