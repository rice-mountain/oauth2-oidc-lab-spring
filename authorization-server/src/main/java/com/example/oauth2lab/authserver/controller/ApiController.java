package com.example.oauth2lab.authserver.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/public-data")
    public Map<String, Object> getPublicData(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is public data from Authorization Server API");
        response.put("timestamp", Instant.now().toString());
        response.put("source", "authorization-server");
        
        if (authentication != null) {
            response.put("clientId", authentication.getName());
            response.put("authorities", authentication.getAuthorities());
        }
        
        return response;
    }

    @GetMapping("/protected-data")
    public Map<String, Object> getProtectedData(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is protected data - requires api.read scope");
        response.put("timestamp", Instant.now().toString());
        response.put("source", "authorization-server");
        response.put("clientId", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("data", Map.of(
            "id", 12345,
            "value", "Sensitive business data",
            "category", "confidential"
        ));
        
        return response;
    }
}
