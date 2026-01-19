package com.example.oauth2lab.client.controller;

import com.example.oauth2lab.client.service.ClientCredentialsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/client-credentials")
public class ClientCredentialsController {

    private final ClientCredentialsService clientCredentialsService;

    public ClientCredentialsController(ClientCredentialsService clientCredentialsService) {
        this.clientCredentialsService = clientCredentialsService;
    }

    @GetMapping("/public-data")
    public Map<String, Object> getPublicData() {
        return clientCredentialsService.callPublicData();
    }

    @GetMapping("/protected-data")
    public Map<String, Object> getProtectedData() {
        return clientCredentialsService.callProtectedData();
    }
}
