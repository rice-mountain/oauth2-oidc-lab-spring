package com.example.oauth2lab.client.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ClientCredentialsService {

    private final WebClient webClient;
    private final String authServerUrl;

    public ClientCredentialsService(
            WebClient webClient,
            @Value("${spring.security.oauth2.client.provider.oauth2-provider.issuer-uri:http://localhost:9000}") String authServerUrl) {
        this.webClient = webClient;
        this.authServerUrl = authServerUrl;
    }

    public Map<String, Object> callPublicData() {
        return webClient
                .get()
                .uri(authServerUrl + "/api/public-data")
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction
                        .clientRegistrationId("machine-client"))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map<String, Object> callProtectedData() {
        return webClient
                .get()
                .uri(authServerUrl + "/api/protected-data")
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction
                        .clientRegistrationId("machine-client"))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
