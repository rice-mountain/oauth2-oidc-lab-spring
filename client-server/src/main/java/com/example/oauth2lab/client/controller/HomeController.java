package com.example.oauth2lab.client.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        
        model.addAttribute("userName", oauth2User.getName());
        model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientName());
        model.addAttribute("userAttributes", oauth2User.getAttributes());
        
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
