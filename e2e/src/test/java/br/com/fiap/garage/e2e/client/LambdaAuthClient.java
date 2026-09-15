package br.com.fiap.garage.e2e.client;

import br.com.fiap.garage.e2e.config.E2eConfig;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class LambdaAuthClient {

    private final String authUrl;
    private final String username;
    private final String password;

    public LambdaAuthClient() {
        this(E2eConfig.getLambdaAuthUrl(), E2eConfig.getAuthUsername(), E2eConfig.getAuthPassword());
    }

    public LambdaAuthClient(String authUrl, String username, String password) {
        this.authUrl = authUrl.replaceAll("/+$", "");
        this.username = username;
        this.password = password;
    }

    public String authenticate() {
        var loginResponse = attemptLogin(username, password);

        if (loginResponse.statusCode() == 200) {
            var accessToken = loginResponse.jsonPath().getString("access_token");
            return "Bearer " + accessToken;
        }

        // If user is not found, attempt self-registration via Lambda then re-authenticate
        if (loginResponse.statusCode() == 404 || loginResponse.statusCode() == 401) {
            var registerResponse = attemptRegister(username, password);
            if (registerResponse.statusCode() == 201 || registerResponse.statusCode() == 409) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                var retryResponse = attemptLogin(username, password);
                if (retryResponse.statusCode() == 200) {
                    var accessToken = retryResponse.jsonPath().getString("access_token");
                    return "Bearer " + accessToken;
                }
            }

            // Fallback: If configured static operator fails, provision a dynamic operator to ensure test resilience
            System.err.println("[WARN] Static operator registration/login failed. Provisioning dynamic operator for E2E...");
            var dynamicCpf = generateValidCpf();
            var dynamicPassword = "DynamicOperator@2026!";
            var dynamicRegisterResponse = attemptRegister(dynamicCpf, dynamicPassword);
            if (dynamicRegisterResponse.statusCode() == 201 || dynamicRegisterResponse.statusCode() == 409) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                var dynamicLoginResponse = attemptLogin(dynamicCpf, dynamicPassword);
                if (dynamicLoginResponse.statusCode() == 200) {
                    var accessToken = dynamicLoginResponse.jsonPath().getString("access_token");
                    return "Bearer " + accessToken;
                }
            }

            throw new IllegalStateException("Registration via Lambda failed for static and dynamic operators. Static HTTP "
                    + registerResponse.statusCode() + ": " + registerResponse.body().asString());
        }

        throw new IllegalStateException("Failed to authenticate with Lambda auth service at "
                + authUrl + "/auth/login. HTTP " + loginResponse.statusCode() + ": " + loginResponse.body().asString());
    }

    private Response attemptLogin(String user, String pass) {
        var payload = Map.of(
                "username", user,
                "password", pass
        );

        return given()
                .filter(new br.com.fiap.garage.e2e.filter.AwsSigV4Filter())
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(authUrl + "/auth/login")
                .then()
                .extract()
                .response();
    }

    private Response attemptRegister(String user, String pass) {
        var cleanCpf = user.replaceAll("\\D", "");
        var payload = Map.of(
                "name", "E2E Operator",
                "email", "e2e_operator_" + cleanCpf + "@garage.com",
                "document", user,
                "password", pass,
                "role", "EMPLOYEE"
        );

        return given()
                .filter(new br.com.fiap.garage.e2e.filter.AwsSigV4Filter())
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(authUrl + "/register")
                .then()
                .extract()
                .response();
    }

    private static String generateValidCpf() {
        var random = new java.util.Random();
        var digits = new int[11];
        for (int i = 0; i < 9; i++) {
            digits[i] = random.nextInt(10);
        }

        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += digits[i] * (10 - i);
        }
        int remainder1 = sum1 % 11;
        digits[9] = (remainder1 < 2) ? 0 : 11 - remainder1;

        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += digits[i] * (11 - i);
        }
        int remainder2 = sum2 % 11;
        digits[10] = (remainder2 < 2) ? 0 : 11 - remainder2;

        return String.format("%d%d%d.%d%d%d.%d%d%d-%d%d",
                digits[0], digits[1], digits[2],
                digits[3], digits[4], digits[5],
                digits[6], digits[7], digits[8],
                digits[9], digits[10]);
    }
}
