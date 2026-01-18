package com.example.oauth2lab.client.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/user")
    public Map<String, Object> getUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("authenticated", authentication.isAuthenticated());
        return response;
    }

    @GetMapping("/data")
    public Map<String, Object> getData(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is protected data from the Resource Server");
        response.put("user", authentication.getName());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
