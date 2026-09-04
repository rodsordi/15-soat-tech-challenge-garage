package br.com.fiap.commons.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("KeycloakJwtAuthenticationConverter Unit Tests")
class KeycloakJwtAuthenticationConverterTest {

    private KeycloakJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakJwtAuthenticationConverter();
    }

    private Jwt createMockJwt(Map<String, Object> claims) {
        return new Jwt(
                "mock-token-value",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "RS256", "typ", "JWT"),
                claims
        );
    }

    @DisplayName("When converting JWT to Authentication Token")
    @Nested
    class Convert {

        @DisplayName("Given a JWT with realm_access roles and preferred_username")
        @Test
        void shouldExtractRolesAndPreferredUsername() {
            var claims = Map.<String, Object>of(
                    "preferred_username", "12345678909",
                    "sub", "user-uuid-12345",
                    "email", "rodrigo.test@example.com",
                    "realm_access", Map.of("roles", List.of("CUSTOMER", "default-roles-garage"))
            );
            var jwt = createMockJwt(claims);

            AbstractAuthenticationToken token = converter.convert(jwt);

            assertThat(token).isNotNull();
            assertThat(token.getName()).isEqualTo("12345678909");
            assertThat(token.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrder("ROLE_CUSTOMER", "ROLE_DEFAULT-ROLES-GARAGE");
        }

        @DisplayName("Given a JWT with multiple business roles")
        @Test
        void shouldExtractMultipleRoles() {
            var claims = Map.<String, Object>of(
                    "preferred_username", "98765432100",
                    "sub", "employee-uuid-67890",
                    "realm_access", Map.of("roles", List.of("EMPLOYEE", "ADMIN"))
            );
            var jwt = createMockJwt(claims);

            AbstractAuthenticationToken token = converter.convert(jwt);

            assertThat(token).isNotNull();
            assertThat(token.getName()).isEqualTo("98765432100");
            assertThat(token.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrder("ROLE_EMPLOYEE", "ROLE_ADMIN");
        }

        @DisplayName("Given a JWT without preferred_username, should fallback to sub")
        @Test
        void shouldFallbackToSubWhenPreferredUsernameMissing() {
            var claims = Map.<String, Object>of(
                    "sub", "user-uuid-fallback",
                    "realm_access", Map.of("roles", List.of("CUSTOMER"))
            );
            var jwt = createMockJwt(claims);

            AbstractAuthenticationToken token = converter.convert(jwt);

            assertThat(token).isNotNull();
            assertThat(token.getName()).isEqualTo("user-uuid-fallback");
            assertThat(token.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_CUSTOMER");
        }

        @DisplayName("Given a JWT without realm_access or empty roles")
        @Test
        void shouldHandleEmptyOrMissingRolesGracefully() {
            var claims = Map.<String, Object>of(
                    "preferred_username", "12345678909",
                    "sub", "user-uuid-no-roles"
            );
            var jwt = createMockJwt(claims);

            AbstractAuthenticationToken token = converter.convert(jwt);

            assertThat(token).isNotNull();
            assertThat(token.getName()).isEqualTo("12345678909");
            assertThat(token.getAuthorities()).isEmpty();
        }

        @DisplayName("Given a JWT with empty roles list in realm_access")
        @Test
        void shouldHandleEmptyRolesList() {
            var claims = Map.<String, Object>of(
                    "preferred_username", "12345678909",
                    "sub", "user-uuid-empty-roles",
                    "realm_access", Map.of("roles", Collections.emptyList())
            );
            var jwt = createMockJwt(claims);

            AbstractAuthenticationToken token = converter.convert(jwt);

            assertThat(token).isNotNull();
            assertThat(token.getName()).isEqualTo("12345678909");
            assertThat(token.getAuthorities()).isEmpty();
        }
    }
}
