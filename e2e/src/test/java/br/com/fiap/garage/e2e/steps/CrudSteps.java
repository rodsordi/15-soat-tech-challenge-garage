package br.com.fiap.garage.e2e.steps;

import br.com.fiap.garage.e2e.context.ScenarioTestContext;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import tools.jackson.databind.json.JsonMapper;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.factory.CustomerDtoFactory.create_CustomerDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory.create_EmployeeDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory.create_InventoryMaterialDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.ServiceDtoFactory.create_ServiceDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.VehicleDtoFactory.create_VehicleDto_Request;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;

public class CrudSteps {

    private final ScenarioTestContext context;

    public CrudSteps(ScenarioTestContext context) {
        this.context = context;
    }

    private final JsonMapper json = JsonMapper.builder()
            .findAndAddModules()
            .build();

    private final Random random = new Random();

    // --- EMPLOYEE CRUD ---
    @Quando("um novo funcionário mecânico é cadastrado com CPF e e-mail válidos")
    public void aNewMechanicEmployeeIsRegisteredWithValidCpfAndEmail() {
        var uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        var request = create_EmployeeDto_Request().valid();
        setField(request, "name", "Employee " + uniqueSuffix);
        setField(request, "email", "emp_" + uniqueSuffix + "@garage.com");
        setField(request, "cpf", generateValidCpf());

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(request))
                .post("/v1/employees")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        if (response.statusCode() == 201) {
            context.setEmployeeId(response.jsonPath().getString("id"));
        }
    }

    @Entao("o funcionário deve ser persistido com status {int}")
    public void theEmployeeShouldBePersistedWithStatus(int expectedStatus) {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(expectedStatus);
        assertThat(context.getEmployeeId()).isNotBlank();
    }

    @E("os dados cadastrais do funcionário devem ser consultados com sucesso por seu identificador")
    public void theEmployeeDataShouldBeSuccessfullyQueriedById() {
        var response = given()
                .header("Authorization", context.getAuthorization())
                .pathParam("employeeId", context.getEmployeeId())
                .get("/v1/employees/{employeeId}")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("id")).isEqualTo(context.getEmployeeId());
    }

    // --- CUSTOMER CRUD ---
    @Quando("um novo cliente é cadastrado com documento e e-mail válidos")
    public void aNewCustomerIsRegisteredWithValidDocumentAndEmail() {
        var uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        var request = create_CustomerDto_Request().valid();
        setField(request, "name", "Customer " + uniqueSuffix);
        setField(request, "email", "customer_" + uniqueSuffix + "@example.com");
        setField(request, "document", generateValidCpf());

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(request))
                .post("/v1/customers")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        if (response.statusCode() == 201) {
            context.setCustomerId(response.jsonPath().getString("id"));
        }
    }

    @Entao("o cliente deve ser persistido com status {int}")
    public void theCustomerShouldBePersistedWithStatus(int expectedStatus) {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(expectedStatus);
        assertThat(context.getCustomerId()).isNotBlank();
    }

    @E("os dados cadastrais do cliente devem ser consultados com sucesso por seu identificador")
    public void theCustomerDataShouldBeSuccessfullyQueriedById() {
        var response = given()
                .header("Authorization", context.getAuthorization())
                .pathParam("customerId", context.getCustomerId())
                .get("/v1/customers/{customerId}")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("id")).isEqualTo(context.getCustomerId());
    }

    // --- VEHICLE CRUD ---
    @Quando("um novo veículo é cadastrado com placa única para o cliente")
    public void aNewVehicleIsRegisteredWithUniquePlateForCustomer() {
        var request = create_VehicleDto_Request().valid();
        var uniquePlate = String.format("XYZ%04d", random.nextInt(10000));
        setField(request, "licensePlate", uniquePlate);

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(request))
                .pathParam("customerId", context.getCustomerId())
                .post("/v1/customers/{customerId}/vehicles")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        if (response.statusCode() == 201) {
            context.setVehicleId(response.jsonPath().getString("id"));
        }
    }

    @Entao("o veículo deve ser persistido com status {int}")
    public void theVehicleShouldBePersistedWithStatus(int expectedStatus) {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(expectedStatus);
        assertThat(context.getVehicleId()).isNotBlank();
    }

    @E("os dados do veículo devem ser consultados com sucesso por seu identificador")
    public void theVehicleDataShouldBeQueriedById() {
        var response = given()
                .header("Authorization", context.getAuthorization())
                .pathParam("vehicleId", context.getVehicleId())
                .get("/v1/vehicles/{vehicleId}")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("id")).isEqualTo(context.getVehicleId());
    }

    // --- MATERIAL CRUD ---
    @Quando("um novo material de estoque é cadastrado com preço e quantidade válidos")
    public void aNewInventoryMaterialIsRegisteredWithValidPriceAndQuantity() {
        var request = create_InventoryMaterialDto_Request().valid();

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(request))
                .post("/v1/inventory-materials")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        if (response.statusCode() == 201) {
            context.setMaterialId(response.jsonPath().getString("id"));
        }
    }

    @Entao("o material deve ser persistido com status {int}")
    public void theMaterialShouldBePersistedWithStatus(int expectedStatus) {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(expectedStatus);
        assertThat(context.getMaterialId()).isNotBlank();
    }

    @E("os dados do material devem ser consultados com sucesso por seu identificador")
    public void theMaterialDataShouldBeQueriedById() {
        var response = given()
                .header("Authorization", context.getAuthorization())
                .pathParam("materialId", context.getMaterialId())
                .get("/v1/inventory-materials/{materialId}")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("id")).isEqualTo(context.getMaterialId());
    }

    // --- SERVICE CRUD ---
    @Quando("um novo serviço é cadastrado vinculado ao material de estoque")
    public void aNewServiceIsRegisteredLinkedToInventoryMaterial() {
        var request = create_ServiceDto_Request().valid();
        setField(request, "materialsIds", Set.of(UUID.fromString(context.getMaterialId())));

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(request))
                .post("/v1/services")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        if (response.statusCode() == 201) {
            context.setServiceId(response.jsonPath().getString("id"));
        }
    }

    @Entao("o serviço deve ser persistido com status {int}")
    public void theServiceShouldBePersistedWithStatus(int expectedStatus) {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(expectedStatus);
        assertThat(context.getServiceId()).isNotBlank();
    }

    @E("os dados do serviço devem ser consultados com sucesso no catálogo")
    public void theServiceDataShouldBeQueriedInCatalog() {
        var response = given()
                .header("Authorization", context.getAuthorization())
                .pathParam("serviceId", context.getServiceId())
                .get("/v1/services/{serviceId}")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("id")).isEqualTo(context.getServiceId());
    }

    private static String generateValidCpf() {
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

        return String.format("%d%d%d.%d%d%d.%d%d%d-%d%d",
                digits[0], digits[1], digits[2],
                digits[3], digits[4], digits[5],
                digits[6], digits[7], digits[8],
                digits[9], digits[10]);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }
}
