package com.example.oauth2lab.client.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/resource-api")
public class ResourceApiController {

    private final WebClient webClient;

    public ResourceApiController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/execute")
    public Map<String, Object> executeResourceApis(
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Call /api/user endpoint
            Map<String, Object> userData = webClient.get()
                    .uri("http://localhost:8081/api/user")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            // Call /api/data endpoint
            Map<String, Object> apiData = webClient.get()
                    .uri("http://localhost:8081/api/data")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            result.put("success", true);
            result.put("userResponse", userData);
            result.put("dataResponse", apiData);
            result.put("clientName", authorizedClient.getClientRegistration().getClientName());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}
