package com.example.oauth2lab.client.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        
        // Get basic user information
        String userName = oauth2User != null ? oauth2User.getName() : "Unknown";
        model.addAttribute("userName", userName);
        model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientName());
        
        // Get user attributes
        Map<String, Object> userAttributes = oauth2User != null ? oauth2User.getAttributes() : new HashMap<>();
        model.addAttribute("userAttributes", userAttributes);
        
        // Check if this is an OIDC flow (has ID Token)
        boolean isOidc = oauth2User instanceof OidcUser;
        model.addAttribute("isOidc", isOidc);
        
        if (isOidc) {
            OidcUser oidcUser = (OidcUser) oauth2User;
            model.addAttribute("idToken", oidcUser.getIdToken().getTokenValue());
            model.addAttribute("idTokenClaims", oidcUser.getIdToken().getClaims());
        }
        
        model.addAttribute("accessToken", authorizedClient.getAccessToken().getTokenValue());
        
        return "dashboard";
    }
}
