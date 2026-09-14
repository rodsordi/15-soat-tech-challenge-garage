package br.com.fiap.garage.e2e.config;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

public final class E2eConfig {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?}");
    private static final Properties properties = new Properties();
    private static String activeEnv;

    static {
        loadProperties();
    }

    private E2eConfig() {
    }

    public static synchronized void loadProperties() {
        properties.clear();

        // 1. Load base properties
        loadResource("application.properties");

        // 2. Identify active environment
        activeEnv = System.getProperty("env",
                System.getenv().getOrDefault("E2E_ENV", properties.getProperty("e2e.env", "prd")));

        // 3. Load environment-specific properties
        loadResource("application-" + activeEnv + ".properties");

        // 4. Configure REST Assured filters
        configureRestAssuredFilters();
    }

    public static boolean isLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("e2e.logging.enabled", "true"));
    }

    public static void configureRestAssuredFilters() {
        if (isLoggingEnabled()) {
            RestAssured.replaceFiltersWith(new RequestLoggingFilter(), new ResponseLoggingFilter());
        } else {
            RestAssured.replaceFiltersWith(java.util.Collections.emptyList());
        }
    }

    private static void loadResource(String filename) {
        try (InputStream in = E2eConfig.class.getClassLoader().getResourceAsStream(filename)) {
            if (in != null) {
                var temp = new Properties();
                temp.load(in);
                temp.forEach((k, v) -> properties.put(k, resolvePlaceholders((String) v)));
            }
        } catch (Exception e) {
            System.err.println("[WARN] E2eConfig failed to load " + filename + ": " + e.getMessage());
        }
    }

    public static String resolvePlaceholders(String value) {
        if (value == null || !value.contains("${")) {
            return value;
        }

        var matcher = PLACEHOLDER_PATTERN.matcher(value);
        var sb = new StringBuilder();
        while (matcher.find()) {
            var varName = matcher.group(1);
            var defaultValue = matcher.group(2) != null ? matcher.group(2) : "";

            var resolvedValue = System.getProperty(varName,
                    System.getenv().getOrDefault(varName, defaultValue));
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(resolvedValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String getActiveEnv() {
        return activeEnv;
    }

    public static String getBaseUri() {
        var fromSystem = System.getProperty("garage.base-uri",
                System.getenv("GARAGE_BASE_URI"));
        if (fromSystem != null && !fromSystem.isBlank() && !"auto".equalsIgnoreCase(fromSystem)) {
            return fromSystem;
        }
        var propVal = resolvePlaceholders(properties.getProperty("garage.base-uri", "http://localhost:8080/api"));
        if (propVal == null || propVal.isBlank() || "auto".equalsIgnoreCase(propVal)) {
            var discovered = br.com.fiap.garage.e2e.aws.AwsEndpointResolver.resolveGarageBaseUri();
            if (discovered != null && !discovered.isBlank()) {
                return discovered;
            }
        }
        return propVal;
    }

    public static String getAuthToken() {
        var fromSystem = System.getProperty("garage.auth.token",
                System.getenv("GARAGE_AUTH_TOKEN"));
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        return resolvePlaceholders(properties.getProperty("garage.auth.token", "Bearer e2e-integration-token"));
    }

    public static String getProperty(String key, String defaultValue) {
        var fromSystem = System.getProperty(key, System.getenv(key));
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        var propVal = properties.getProperty(key);
        return propVal != null ? resolvePlaceholders(propVal) : defaultValue;
    }

    public static String getAuthType() {
        return getProperty("garage.auth.type", "bearer-mock");
    }

    public static String getLambdaAuthUrl() {
        var fromSystem = System.getProperty("garage.auth.lambda.url",
                System.getenv("GARAGE_AUTH_LAMBDA_URL"));
        if (fromSystem != null && !fromSystem.isBlank() && !"auto".equalsIgnoreCase(fromSystem)) {
            return fromSystem;
        }
        var propVal = getProperty("garage.auth.lambda.url", "auto");
        if (propVal == null || propVal.isBlank() || "auto".equalsIgnoreCase(propVal)) {
            var discovered = br.com.fiap.garage.e2e.aws.AwsEndpointResolver.resolveLambdaAuthUrl();
            if (discovered != null && !discovered.isBlank()) {
                return discovered;
            }
        }
        return propVal;
    }

    public static String getAuthUsername() {
        return getProperty("garage.auth.lambda.username", "529.982.247-25");
    }

    public static String getAuthPassword() {
        return getProperty("garage.auth.lambda.password", "SenhaForte@2026");
    }
}

