package br.com.fiap.garage.manual;

import br.com.fiap.garage.application.v1.dto.CustomerDto;
import br.com.fiap.garage.application.v1.dto.EmployeeDto;
import br.com.fiap.garage.application.v1.dto.InventoryMaterialDto;
import br.com.fiap.garage.application.v1.dto.ServiceDto;
import br.com.fiap.garage.application.v1.dto.VehicleDto;
import br.com.fiap.garage.application.v1.dto.WorkOrderDto;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.text.MessageFormat;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.factory.CustomerDtoFactory.create_CustomerDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory.create_EmployeeDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory.create_InventoryMaterialDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.ServiceDtoFactory.create_ServiceDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.VehicleDtoFactory.create_VehicleDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory.create_WorkOrderDto_Request;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * Manual integration test executing the complete end-to-end Work Order lifecycle
 * directly against the live AWS cloud infrastructure (Amazon EKS, Amazon RDS PostgreSQL, AWS API Gateway).
 *
 * Replicates the complete workflow originally demonstrated in {@link br.com.fiap.garage.iandt.use_case.CompleteWorkOrderCreationUseCaseTest}.
 */
@Disabled("Manual test intended to be executed on-demand against live AWS cloud infrastructure")
@DisplayName("AWS Infrastructure - Complete Work Order Creation Flow Test")
public class CompleteWorkOrderCreationAwsManualTest {

    private static final String DEFAULT_AWS_BASE_URI = "https://uk7w1b0vue.execute-api.us-east-1.amazonaws.com/api";

    private static final JsonMapper json = JsonMapper.builder()
            .findAndAddModules()
            .build();

    private static final Random random = new Random();

    private static final String DEFAULT_KEYCLOAK_TOKEN_URI = "http://a3c63e7e0fb384a4c8c29ea5e43c9e17-1683280248.us-east-1.elb.amazonaws.com:8080/realms/garage/protocol/openid-connect/token";

    private String authorization;

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = System.getProperty("garage.base-uri",
                System.getenv().getOrDefault("GARAGE_BASE_URI", DEFAULT_AWS_BASE_URI));
    }

    @BeforeEach
    void authenticate() {
        var keycloakTokenUri = System.getProperty("garage.keycloak-token-uri",
                System.getenv().getOrDefault("GARAGE_KEYCLOAK_TOKEN_URI", DEFAULT_KEYCLOAK_TOKEN_URI));
        var username = System.getProperty("garage.keycloak-username",
                System.getenv().getOrDefault("GARAGE_KEYCLOAK_USERNAME", "12345678909"));
        var password = System.getProperty("garage.keycloak-password",
                System.getenv().getOrDefault("GARAGE_KEYCLOAK_PASSWORD", "Test@1234"));
        var clientId = System.getProperty("garage.keycloak-client-id",
                System.getenv().getOrDefault("GARAGE_KEYCLOAK_CLIENT_ID", "garage-client"));

        var tokenResponse = given()
                .log().all()
                .baseUri(keycloakTokenUri)
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("client_id", clientId)
                .formParam("username", username)
                .formParam("password", password)
                .post()
                .then()
                .log().all()
                .extract()
                .response();

        assertThat(tokenResponse.statusCode()).isEqualTo(200);
        var token = tokenResponse.jsonPath().getString("access_token");
        assertThat(token).isNotBlank();

        authorization = MessageFormat.format("Bearer {0}", token);
    }

    @DisplayName("When executing the complete work order creation flow against AWS infrastructure")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a workOrder with all fields, should execute the entire lifecycle in AWS")
            @Test
            void shouldExecuteCompleteWorkOrderLifecycleInAws() {
                Response response;

                System.out.println("\n====== [AWS] Creating a new work order ======\n");
                response = consumePostWorkOrders();
                var workOrderId = response.jsonPath().getString("id");
                assertThat(workOrderId).isNotBlank();

                System.out.println("\n====== [AWS] Sending to the mechanic to diagnose the problems ======\n");
                consumePatchWorkOrders_updateStatus(workOrderId, DIAGNOSING);

                System.out.println("\n====== [AWS] Finished diagnosis, waiting for customer approval ======\n");
                consumePatchWorkOrders_updateStatus(workOrderId, WAITING_FOR_APPROVAL);

                System.out.println("\n====== [AWS] Checking the e-mail notification informing the estimated service ======\n");
                consumeGetNotifications(workOrderId);

                System.out.println("\n====== [AWS] Customer approval simulation ======\n");
                response = consumePatchWorkOrders_updateStatus(workOrderId, EXECUTING);
                var serviceId = response.jsonPath().getString("estimatedServices[0].id");
                assertThat(serviceId).isNotBlank();

                System.out.println("\n====== [AWS] Finishing the requested service ======\n");
                consumePatchWorkOrders_finishService(workOrderId, serviceId);

                System.out.println("\n====== [AWS] Finished the work order ======\n");
                consumePatchWorkOrders_updateStatus(workOrderId, FINISHED);

                System.out.println("\n====== [AWS] Released the vehicle to the customer ======\n");
                consumePatchWorkOrders_updateStatus(workOrderId, RELEASED);

                System.out.println("\n====== [AWS] Simulate routine started of Services avg time calculation ======\n");
                consumeGetServices_calculateAverageTime();

                System.out.println("\n====== [AWS] Query services average time ======\n");
                consumeGetServices_assertAverageTime(serviceId);
            }
        }
    }

    private Response consumePostWorkOrders() {
        var uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);

        // 1. Create unique Customer
        var customerRequest = create_CustomerDto_Request().valid();
        setField(customerRequest, "username", "customer_" + uniqueSuffix + "@example.com");
        setField(customerRequest, "email", "customer_" + uniqueSuffix + "@example.com");
        setField(customerRequest, "document", generateValidCpf());
        var customerResponse = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(customerRequest))
                .post("/v1/customers")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(customerResponse.statusCode()).isEqualTo(201);
        var customerId = customerResponse.jsonPath().getString("id");

        // 2. Create unique Vehicle
        var vehicleRequest = create_VehicleDto_Request().valid();
        var uniquePlate = String.format("AWS%04d", random.nextInt(10000));
        setField(vehicleRequest, "licensePlate", uniquePlate);
        var vehicleResponse = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(vehicleRequest))
                .pathParam("customerId", customerId)
                .post("/v1/customers/{customerId}/vehicles")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(vehicleResponse.statusCode()).isEqualTo(201);
        var vehicleId = vehicleResponse.jsonPath().getString("id");

        // 3. Create unique Mechanic Employee
        var mechanicRequest = create_EmployeeDto_Request().valid();
        setField(mechanicRequest, "username", "mechanic_" + uniqueSuffix + "@garage.com");
        setField(mechanicRequest, "email", "mechanic_" + uniqueSuffix + "@garage.com");
        setField(mechanicRequest, "cpf", generateValidCpf());
        var mechanicResponse = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(mechanicRequest))
                .post("/v1/employees")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(mechanicResponse.statusCode()).isEqualTo(201);
        var employeeId = mechanicResponse.jsonPath().getString("id");

        // 4. Create Inventory Material
        var materialRequest = create_InventoryMaterialDto_Request().valid();
        var materialResponse = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(materialRequest))
                .post("/v1/inventory-materials")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(materialResponse.statusCode()).isEqualTo(201);
        var materialId = materialResponse.jsonPath().getString("id");

        // 5. Create Service
        var serviceRequest = create_ServiceDto_Request().valid();
        setField(serviceRequest, "materialsIds", Set.of(UUID.fromString(materialId)));
        var serviceResponse = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(serviceRequest))
                .post("/v1/services")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(serviceResponse.statusCode()).isEqualTo(201);
        var serviceId = serviceResponse.jsonPath().getString("id");

        // 6. Create Work Order
        var workOrderRequest = create_WorkOrderDto_Request().valid();
        setField(workOrderRequest, "vehicleId", UUID.fromString(vehicleId));
        setField(workOrderRequest, "employeeId", UUID.fromString(employeeId));
        setField(workOrderRequest, "servicesIds", Set.of(UUID.fromString(serviceId)));

        var workOrderResponse = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(workOrderRequest))
                .post("/v1/work-orders")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(workOrderResponse.statusCode()).isEqualTo(201);
        return workOrderResponse;
    }

    private Response consumeGetNotifications(String workOrderId) {
        Response response = null;
        for (int i = 0; i < 15; i++) {
            response = given()
                    .log().all()
                    .header("Authorization", authorization)
                    .param("externalId", workOrderId)
                    .get("/v1/notifications")
                    .then()
                    .log().all()
                    .extract()
                    .response();
            if (response.statusCode() == 200) {
                return response;
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        assertThat(response).isNotNull();
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    private Response consumePatchWorkOrders_finishService(String workOrderId, String serviceId) {
        var requestBody = WorkOrderDto.PatchRequest.builder()
                .finishedServiceId(UUID.fromString(serviceId))
                .build();

        var response = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .pathParam("workOrderId", workOrderId)
                .patch("/v1/work-orders/{workOrderId}")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("estimatedServices[0].finishedAt")).isNotBlank();
        return response;
    }

    private Response consumePatchWorkOrders_updateStatus(String workOrderId, WorkOrderStatus status) {
        var requestBody = WorkOrderDto.PatchRequest.builder()
                .status(status)
                .build();

        var response = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .pathParam("workOrderId", workOrderId)
                .patch("/v1/work-orders/{workOrderId}")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo(status.name());

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return response;
    }

    private Response consumeGetServices_calculateAverageTime() {
        var response = given()
                .log().all()
                .header("Authorization", authorization)
                .get("/v1/services/calculateAverageTime")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(response.statusCode()).isEqualTo(204);
        return response;
    }

    private Response consumeGetServices_assertAverageTime(String serviceId) {
        var response = given()
                .log().all()
                .header("Authorization", authorization)
                .pathParam("serviceId", serviceId)
                .get("/v1/services/{serviceId}")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getLong("averageTimeInMinutes")).isNotNull();
        return response;
    }

    private static String generateValidCpf() {
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