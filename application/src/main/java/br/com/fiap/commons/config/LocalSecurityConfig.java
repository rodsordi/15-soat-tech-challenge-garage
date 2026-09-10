package br.com.fiap.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Configuration
@Profile("local")
public class LocalSecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("sub", "local-e2e-subject")
                .claim("preferred_username", "90443471010")
                .claim("email", "admin@garage.com")
                .claim("realm_access", Map.of("roles", List.of("EMPLOYEE", "ADMIN", "CUSTOMER")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
    }
}
