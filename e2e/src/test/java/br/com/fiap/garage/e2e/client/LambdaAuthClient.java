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
        var loginResponse = attemptLogin();

        if (loginResponse.statusCode() == 200) {
            var accessToken = loginResponse.jsonPath().getString("access_token");
            return "Bearer " + accessToken;
        }

        // If user is not found, attempt self-registration via Lambda then re-authenticate
        if (loginResponse.statusCode() == 404 || loginResponse.statusCode() == 401) {
            var registerResponse = attemptRegister();
            if (registerResponse.statusCode() != 201 && registerResponse.statusCode() != 409) {
                throw new IllegalStateException("Registration via Lambda failed. HTTP "
                        + registerResponse.statusCode() + ": " + registerResponse.body().asString());
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var retryResponse = attemptLogin();
            if (retryResponse.statusCode() == 200) {
                var accessToken = retryResponse.jsonPath().getString("access_token");
                return "Bearer " + accessToken;
            }
            throw new IllegalStateException("Authentication failed after registration attempt. HTTP "
                    + retryResponse.statusCode() + ": " + retryResponse.body().asString());
        }

        throw new IllegalStateException("Failed to authenticate with Lambda auth service at "
                + authUrl + "/auth/login. HTTP " + loginResponse.statusCode() + ": " + loginResponse.body().asString());
    }

    private Response attemptLogin() {
        var payload = Map.of(
                "username", username,
                "password", password
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

    private Response attemptRegister() {
        var cleanCpf = username.replaceAll("\\D", "");
        var payload = Map.of(
                "name", "E2E Operator",
                "email", "e2e_operator_" + cleanCpf + "@garage.com",
                "document", username,
                "password", password,
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


}
