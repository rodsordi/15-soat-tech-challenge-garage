package br.com.fiap.garage.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@TestConfiguration
public class SecurityTestConfig {

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("sub", "integration-test-subject")
                .claim("preferred_username", "90443471010")
                .claim("email", "admin@garage.com")
                .claim("realm_access", Map.of("roles", List.of("EMPLOYEE", "ADMIN", "CUSTOMER")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
    }
}
