package br.com.fiap.garage.e2e.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("E2E Configuration Tests")
class E2eConfigTest {

    @BeforeEach
    @AfterEach
    void resetSystemProperties() {
        System.clearProperty("env");
        System.clearProperty("garage.base-uri");
        System.clearProperty("garage.auth.token");
        System.clearProperty("E2E_GARAGE_BASE_URI");
        E2eConfig.loadProperties();
    }

    @Nested
    @DisplayName("Local Environment Profile")
    class LocalEnvironmentTests {

        @Test
        @DisplayName("Should load default local properties when no environment is specified")
        void shouldLoadDefaultLocalProperties() {
            System.clearProperty("env");
            E2eConfig.loadProperties();

            assertThat(E2eConfig.getActiveEnv()).isEqualTo("local");
            assertThat(E2eConfig.getBaseUri()).isEqualTo("http://localhost:8080/api");
            assertThat(E2eConfig.getAuthToken()).isEqualTo("Bearer e2e-integration-token");
        }

        @Test
        @DisplayName("Should override base URI via system property")
        void shouldOverrideBaseUriViaSystemProperty() {
            System.setProperty("garage.base-uri", "http://custom-host:9090/api");
            E2eConfig.loadProperties();

            assertThat(E2eConfig.getBaseUri()).isEqualTo("http://custom-host:9090/api");
        }
    }

    @Nested
    @DisplayName("Production Environment Profile")
    class ProductionEnvironmentTests {

        @Test
        @DisplayName("Should load PRD properties with defaults when env is prd")
        void shouldLoadPrdPropertiesWithDefaults() {
            System.setProperty("env", "prd");
            E2eConfig.loadProperties();

            assertThat(E2eConfig.getActiveEnv()).isEqualTo("prd");
            assertThat(E2eConfig.getBaseUri()).isEqualTo("https://6t8e18w3f8.execute-api.us-east-1.amazonaws.com/api");
            assertThat(E2eConfig.getAuthType()).isEqualTo("lambda");
            assertThat(E2eConfig.getLambdaAuthUrl()).isEqualTo("https://xr26z2f6imttw4zwmiu7r4unlq0lzurl.lambda-url.us-east-1.on.aws");
        }


        @Test
        @DisplayName("Should resolve placeholder with environment variable or system property")
        void shouldResolvePlaceholderWithSystemProperty() {
            System.setProperty("env", "prd");
            System.setProperty("E2E_GARAGE_BASE_URI", "https://k8s.garage.internal/api");
            E2eConfig.loadProperties();

            assertThat(E2eConfig.getBaseUri()).isEqualTo("https://k8s.garage.internal/api");
        }
    }

    @Nested
    @DisplayName("Placeholder Resolution")
    class PlaceholderTests {

        @Test
        @DisplayName("Should resolve default value when variable is absent")
        void shouldResolveDefaultValueWhenVariableIsAbsent() {
            var resolved = E2eConfig.resolvePlaceholders("${NON_EXISTENT_VAR:my-default-value}");
            assertThat(resolved).isEqualTo("my-default-value");
        }

        @Test
        @DisplayName("Should return literal when no placeholder exists")
        void shouldReturnLiteralWhenNoPlaceholderExists() {
            var literal = "plain-string-value";
            assertThat(E2eConfig.resolvePlaceholders(literal)).isEqualTo(literal);
        }
    }
}
