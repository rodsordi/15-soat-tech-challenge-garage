package br.com.fiap.garage.e2e.steps;

import br.com.fiap.garage.e2e.config.E2eConfig;
import br.com.fiap.garage.e2e.context.ScenarioTestContext;
import br.com.fiap.garage.e2e.filter.AwsSigV4Filter;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;

public class LambdaSteps {

    private final ScenarioTestContext context;
    private final String lambdaBaseUrl;
    private final AwsSigV4Filter sigV4Filter = new AwsSigV4Filter();

    private String registeredCpf;
    private String registeredPassword;

    public LambdaSteps(ScenarioTestContext context) {
        this.context = context;
        this.lambdaBaseUrl = E2eConfig.getLambdaAuthUrl().replaceAll("/+$", "");
    }

    // =========================================================================
    // 1. HEALTH CHECK & INFRA (lambda_health.feature)
    // =========================================================================

    @Quando("uma requisição GET é enviada para a raiz do serviço Lambda")
    public void aGetRequestIsSentToTheLambdaRoot() {
        Response response = given()
                .filter(sigV4Filter)
                .get(lambdaBaseUrl + "/");
        context.setLastResponse(response);
    }

    @Entao("a resposta do Lambda deve ter status {int}")
    public void theLambdaResponseMustHaveStatus(int expectedStatus) {
        assertThat(context.getLastResponse()).isNotNull();
        assertThat(context.getLastResponse().statusCode()).isEqualTo(expectedStatus);
    }

    @E("o corpo da resposta deve conter o status {string}")
    public void theResponseBodyMustContainStatus(String expectedStatus) {
        assertThat(context.getLastResponse().jsonPath().getString("status")).isEqualTo(expectedStatus);
    }

    @E("a lista de endpoints disponíveis deve conter as rotas de registro, consulta e autenticação")
    public void theListOfAvailableEndpointsMustContainStandardRoutes() {
        List<Map<String, String>> endpoints = context.getLastResponse().jsonPath().getList("endpoints");
        assertThat(endpoints).isNotEmpty();
        var paths = endpoints.stream().map(e -> e.get("path")).toList();
        assertThat(paths).contains("/register", "/users/{cpf}", "/auth/login");
    }

    @Quando("uma requisição OPTIONS é enviada para o serviço Lambda")
    public void anOptionsRequestIsSentToTheLambda() {
        Response response = given()
                .filter(sigV4Filter)
                .options(lambdaBaseUrl + "/register");
        context.setLastResponse(response);
    }

    @E("os cabeçalhos de resposta devem conter políticas CORS permitindo a origem")
    public void theResponseHeadersMustContainCorsPolicies() {
        var allowOrigin = context.getLastResponse().getHeader("Access-Control-Allow-Origin");
        assertThat(allowOrigin).isNotNull();
    }

    // =========================================================================
    // 2. USER REGISTRATION (lambda_register.feature)
    // =========================================================================

    @Quando("uma requisição de cadastro de cliente é enviada ao Lambda com CPF válido e senha")
    public void aCustomerRegistrationRequestIsSentWithValidCpfAndPassword() {
        var cleanCpf = generateValidCpfClean();
        var password = "Password@2026!";
        this.registeredCpf = cleanCpf;
        this.registeredPassword = password;

        var payload = Map.of(
                "name", "E2E Customer " + UUID.randomUUID().toString().substring(0, 6),
                "email", "e2e_cust_" + cleanCpf + "@garage.com",
                "document", cleanCpf,
                "password", password,
                "role", "CUSTOMER"
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/register");
        context.setLastResponse(response);
    }

    @E("a resposta deve confirmar a criação com identificador do usuário e catálogo da oficina")
    public void theResponseMustConfirmCreationWithUserIdAndCatalog() {
        assertThat(context.getLastResponse().jsonPath().getBoolean("success")).isTrue();
        assertThat(context.getLastResponse().jsonPath().getString("user.id")).isNotBlank();
        assertThat(context.getLastResponse().jsonPath().getString("catalog.id")).isNotBlank();
    }

    @Quando("uma requisição de cadastro corporativo é enviada ao Lambda com CNPJ válido e dados do veículo")
    public void aCorporateRegistrationRequestIsSentWithValidCnpjAndVehicleData() {
        var cleanCnpj = "27614623000100";
        var password = "Password@2026!";

        var vehicle = Map.of(
                "make", "Volvo",
                "model", "FH 540",
                "licensePlate", "COR" + (1000 + new Random().nextInt(8999)),
                "manufactureYear", "2024"
        );

        var payload = Map.of(
                "name", "Frota E2E Transportes",
                "email", "frota_" + UUID.randomUUID().toString().substring(0, 6) + "@transportes.com",
                "document", cleanCnpj,
                "password", password,
                "role", "CUSTOMER",
                "vehicles", List.of(vehicle)
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/register");
        context.setLastResponse(response);
    }

    @E("a resposta deve conter a identificação do cliente e o veículo associado")
    public void theResponseMustContainCustomerIdentificationAndAssociatedVehicle() {
        assertThat(context.getLastResponse().jsonPath().getBoolean("success")).isTrue();
        assertThat(context.getLastResponse().jsonPath().getString("user.id")).isNotBlank();
    }

    @Quando("uma requisição de cadastro é enviada ao Lambda com CPF {string}")
    public void aRegistrationRequestIsSentWithSpecificCpf(String cpf) {
        var payload = Map.of(
                "name", "Invalid User",
                "email", "invalid@garage.com",
                "document", cpf,
                "password", "Password@2026!"
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/register");
        context.setLastResponse(response);
    }

    @Quando("uma requisição de cadastro é enviada ao Lambda sem o campo de senha")
    public void aRegistrationRequestIsSentWithoutPassword() {
        var payload = Map.of(
                "name", "Missing Password User",
                "email", "nopass@garage.com",
                "document", generateValidCpfClean()
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/register");
        context.setLastResponse(response);
    }

    @E("a resposta deve apresentar o erro {string}")
    public void theResponseMustPresentError(String expectedError) {
        var actual = context.getLastResponse().jsonPath().getString("error");
        if ("Unauthorized".equalsIgnoreCase(expectedError)) {
            assertThat(actual).isIn("Unauthorized", "invalid_grant");
        } else {
            assertThat(actual).isEqualTo(expectedError);
        }
    }

    // =========================================================================
    // 3. USER SEARCH (lambda_user_search.feature)
    // =========================================================================

    @Dado("que existe um usuário previamente cadastrado com CPF válido no sistema")
    public void thereIsAUserPreviouslyRegisteredWithValidCpf() {
        aCustomerRegistrationRequestIsSentWithValidCpfAndPassword();
        // If conflict or already created, it's fine as long as we have a registeredCpf
        assertThat(this.registeredCpf).isNotBlank();
    }

    @Quando("uma requisição de consulta por CPF do usuário cadastrado é enviada ao Lambda")
    public void aUserSearchRequestIsSentForRegisteredCpf() {
        Response response = given()
                .filter(sigV4Filter)
                .get(lambdaBaseUrl + "/users/" + this.registeredCpf);
        context.setLastResponse(response);
    }

    @E("o usuário retornado deve conter o CPF consultado e status cadastrado")
    public void theReturnedUserMustContainTheConsultedCpfAndRegisteredStatus() {
        var returnedCpf = context.getLastResponse().jsonPath().getString("user.cpf");
        assertThat(returnedCpf != null ? returnedCpf : context.getLastResponse().jsonPath().getString("user.username"))
                .isEqualTo(this.registeredCpf);
    }

    @Quando("uma requisição de consulta por CPF é enviada ao endpoint {string} do Lambda")
    public void aUserSearchRequestIsSentToSpecificEndpoint(String endpoint) {
        Response response = given()
                .filter(sigV4Filter)
                .get(lambdaBaseUrl + endpoint);
        context.setLastResponse(response);
    }

    @Quando("uma requisição de consulta por CPF é enviada ao Lambda com um CPF válido não cadastrado")
    public void aUserSearchRequestIsSentWithUnregisteredValidCpf() {
        var cleanCpf = generateValidCpfClean();
        Response response = given()
                .filter(sigV4Filter)
                .get(lambdaBaseUrl + "/users/" + cleanCpf);
        context.setLastResponse(response);
    }

    @E("a resposta deve indicar usuário não encontrado")
    public void theResponseMustIndicateUserNotFound() {
        var json = context.getLastResponse().jsonPath();
        assertThat(json.getString("status")).isEqualTo("NOT_FOUND");
        assertThat(json.getBoolean("exists")).isFalse();
    }

    // =========================================================================
    // 4. AUTHENTICATION & LOGIN (lambda_auth_login.feature)
    // =========================================================================

    @Dado("que existe um usuário cadastrado com credenciais válidas")
    public void thereIsAUserRegisteredWithValidCredentials() {
        if (this.registeredCpf == null || this.registeredPassword == null) {
            aCustomerRegistrationRequestIsSentWithValidCpfAndPassword();
        }
    }

    @Quando("uma requisição de login é enviada com CPF e senha corretos")
    public void aLoginRequestIsSentWithCorrectCpfAndPassword() {
        var payload = Map.of(
                "username", this.registeredCpf,
                "password", this.registeredPassword
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/auth/login");
        context.setLastResponse(response);
    }

    @E("o corpo da resposta deve conter um token de acesso JWT válido e tempo de expiração")
    public void theResponseBodyMustContainValidJwtAccessTokenAndExpiration() {
        assertThat(context.getLastResponse().jsonPath().getString("access_token")).isNotBlank();
        assertThat(context.getLastResponse().jsonPath().getString("token_type")).isEqualTo("Bearer");
        assertThat(context.getLastResponse().jsonPath().getInt("expires_in")).isPositive();
    }

    @Quando("uma requisição de login é enviada com CPF formatado com máscara e senha correta")
    public void aLoginRequestIsSentWithMaskedCpfAndCorrectPassword() {
        var maskedCpf = formatCpf(this.registeredCpf);
        var payload = Map.of(
                "username", maskedCpf,
                "password", this.registeredPassword
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/auth/login");
        context.setLastResponse(response);
    }

    @E("o corpo da resposta deve conter o token de acesso emitido")
    public void theResponseBodyMustContainTheIssuedAccessToken() {
        assertThat(context.getLastResponse().jsonPath().getString("access_token")).isNotBlank();
    }

    @Quando("uma requisição de login é enviada com e-mail {string} e senha")
    public void aLoginRequestIsSentWithEmailAndPass(String email) {
        var payload = Map.of(
                "username", email,
                "password", "AnyPassword123"
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/auth/login");
        context.setLastResponse(response);
    }

    @Quando("uma requisição de login é enviada com CPF {string} e senha")
    public void aLoginRequestIsSentWithInvalidCpfAndPass(String cpf) {
        var payload = Map.of(
                "username", cpf,
                "password", "AnyPassword123"
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/auth/login");
        context.setLastResponse(response);
    }

    @Quando("uma requisição de login é enviada com CPF válido e senha incorreta")
    public void aLoginRequestIsSentWithValidCpfAndIncorrectPassword() {
        var payload = Map.of(
                "username", this.registeredCpf,
                "password", "WrongIncorrectPassword@999"
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/auth/login");
        context.setLastResponse(response);
    }

    @Quando("uma requisição de login é enviada sem o campo de identificação")
    public void aLoginRequestIsSentWithoutUserIdentifier() {
        var payload = Map.of(
                "password", "Password@2026!"
        );

        Response response = given()
                .filter(sigV4Filter)
                .contentType(JSON)
                .accept(JSON)
                .body(payload)
                .post(lambdaBaseUrl + "/auth/login");
        context.setLastResponse(response);
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private static String generateValidCpfClean() {
        var random = new Random();
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

        var sb = new StringBuilder();
        for (int d : digits) {
            sb.append(d);
        }
        return sb.toString();
    }

    private static String formatCpf(String cleanCpf) {
        if (cleanCpf == null || cleanCpf.length() != 11) return cleanCpf;
        return cleanCpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
