package com.example.oauth2lab.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.client.provider.oauth2-provider.issuer-uri:http://localhost:9000}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/sequences", "/login/**", "/error", "/webjars/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/")
                        .defaultSuccessUrl("/dashboard", true)
                )
                .oauth2Client(oauth2 -> {})
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                this.oauth2ClientRegistration(),
                this.oidcClientRegistration()
        );
    }

    // OAuth2-only client configuration (no openid scope)
    // Note: Both clients share the same credentials on the authorization server
    // to demonstrate different flow behaviors with the same client
    private ClientRegistration oauth2ClientRegistration() {
        return ClientRegistration.withRegistrationId("oauth2-client")
                .clientId("oauth2-client-app")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("read", "write")
                .authorizationUri(issuerUri + "/oauth2/authorize")
                .tokenUri(issuerUri + "/oauth2/token")
                .userInfoUri(issuerUri + "/userinfo")
                .userNameAttributeName("sub")
                .clientName("OAuth2 Client (OAuth2 Only)")
                .build();
    }

    private ClientRegistration oidcClientRegistration() {
        return ClientRegistration.withRegistrationId("oidc-client")
                .clientId("oauth2-client-app")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "read", "write")
                .authorizationUri(issuerUri + "/oauth2/authorize")
                .tokenUri(issuerUri + "/oauth2/token")
                .userInfoUri(issuerUri + "/userinfo")
                .jwkSetUri(issuerUri + "/oauth2/jwks")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .clientName("OIDC Client (with ID Token)")
                .build();
    }
}
