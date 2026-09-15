package br.com.fiap.garage.e2e.steps;

import br.com.fiap.garage.application.v1.dto.WorkOrderDto;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import br.com.fiap.garage.e2e.client.LambdaAuthClient;
import br.com.fiap.garage.e2e.config.E2eConfig;
import br.com.fiap.garage.e2e.context.ScenarioTestContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.factory.CustomerDtoFactory.create_CustomerDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory.create_EmployeeDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory.create_InventoryMaterialDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.ServiceDtoFactory.create_ServiceDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.VehicleDtoFactory.create_VehicleDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory.create_WorkOrderDto_Request;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class WorkOrderLifecycleSteps {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderLifecycleSteps.class);

    private final ScenarioTestContext context;

    public WorkOrderLifecycleSteps(ScenarioTestContext context) {
        this.context = context;
    }

    private final JsonMapper json = JsonMapper.builder()
            .findAndAddModules()
            .build();

    private final Random random = new Random();

    @Dado("que o sistema da oficina está em execução e operacional")
    public void theGarageSystemIsRunningAndOperational() {
        var response = given()
                .get("/actuator/health")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isIn(200, 204);
    }

    @Dado("que o operador autentica no sistema através do serviço de autenticação")
    public void theOperatorAuthenticatesViaAuthService() {
        if ("lambda".equalsIgnoreCase(E2eConfig.getAuthType())) {
            var lambdaAuthClient = new LambdaAuthClient();
            var token = lambdaAuthClient.authenticate();
            context.setAuthorization(token);
        } else {
            context.setAuthorization(E2eConfig.getAuthToken());
        }
    }


    @Dado("um cliente cadastrado com documento e e-mail únicos")
    public void aRegisteredCustomerWithUniqueDocumentAndEmail() {
        var uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        var customerRequest = create_CustomerDto_Request().valid();
        setField(customerRequest, "name", "Customer " + uniqueSuffix);
        setField(customerRequest, "email", "customer_" + uniqueSuffix + "@example.com");
        setField(customerRequest, "document", generateValidCpf());

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(customerRequest))
                .post("/v1/customers")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(201);
        context.setCustomerId(response.jsonPath().getString("id"));
        assertThat(context.getCustomerId()).isNotBlank();
    }

    @E("um veículo cadastrado associado ao cliente")
    public void aRegisteredVehicleAssociatedWithTheCustomer() {
        var vehicleRequest = create_VehicleDto_Request().valid();
        var uniquePlate = String.format("XYZ%04d", random.nextInt(10000));
        setField(vehicleRequest, "licensePlate", uniquePlate);

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(vehicleRequest))
                .pathParam("customerId", context.getCustomerId())
                .post("/v1/customers/{customerId}/vehicles")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(201);
        context.setVehicleId(response.jsonPath().getString("id"));
        assertThat(context.getVehicleId()).isNotBlank();
    }

    @E("um funcionário mecânico cadastrado")
    public void aRegisteredMechanicEmployee() {
        var uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        var mechanicRequest = create_EmployeeDto_Request().valid();
        setField(mechanicRequest, "name", "Mechanic " + uniqueSuffix);
        setField(mechanicRequest, "email", "mechanic_" + uniqueSuffix + "@garage.com");
        setField(mechanicRequest, "cpf", generateValidCpf());

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(mechanicRequest))
                .post("/v1/employees")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(201);
        context.setEmployeeId(response.jsonPath().getString("id"));
        assertThat(context.getEmployeeId()).isNotBlank();
    }

    @E("um material de estoque cadastrado")
    public void aRegisteredInventoryMaterial() {
        var materialRequest = create_InventoryMaterialDto_Request().valid();

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(materialRequest))
                .post("/v1/inventory-materials")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(201);
        context.setMaterialId(response.jsonPath().getString("id"));
        assertThat(context.getMaterialId()).isNotBlank();
    }

    @E("um serviço cadastrado vinculado ao material")
    public void aRegisteredServiceLinkedToTheMaterial() {
        var serviceRequest = create_ServiceDto_Request().valid();
        setField(serviceRequest, "materialsIds", Set.of(UUID.fromString(context.getMaterialId())));

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(serviceRequest))
                .post("/v1/services")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(201);
        context.setServiceId(response.jsonPath().getString("id"));
        assertThat(context.getServiceId()).isNotBlank();
    }

    @Quando("uma nova ordem de serviço é criada para o veículo e mecânico")
    public void aNewWorkOrderIsCreatedForTheVehicleAndMechanic() {
        var workOrderRequest = create_WorkOrderDto_Request().valid();
        setField(workOrderRequest, "vehicleId", UUID.fromString(context.getVehicleId()));
        setField(workOrderRequest, "employeeId", UUID.fromString(context.getEmployeeId()));
        setField(workOrderRequest, "servicesIds", Set.of(UUID.fromString(context.getServiceId())));

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(workOrderRequest))
                .post("/v1/work-orders")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        if (response.statusCode() == 201) {
            context.setWorkOrderId(response.jsonPath().getString("id"));
        }
    }

    @Entao("a ordem de serviço deve ser criada com o status {string}")
    public void theWorkOrderShouldBeCreatedWithStatus(String expectedStatus) {
        var response = context.getLastResponse();
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("status")).isEqualTo(expectedStatus);
    }

    @Quando("o mecânico inicia o diagnóstico da ordem de serviço")
    public void theMechanicStartsDiagnosingTheWorkOrder() {
        updateWorkOrderStatus(WorkOrderStatus.DIAGNOSING);
    }

    @Entao("o status da ordem de serviço deve ser atualizado para {string}")
    public void theWorkOrderStatusShouldBeUpdatedTo(String expectedStatus) {
        var response = context.getLastResponse();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo(expectedStatus);
    }

    @Quando("o diagnóstico é concluído e aguarda aprovação do cliente")
    public void theDiagnosisIsFinishedAndAwaitsCustomerApproval() {
        updateWorkOrderStatus(WorkOrderStatus.WAITING_FOR_APPROVAL);
    }

    @E("uma notificação deve ser gerada para a ordem de serviço")
    public void aNotificationShouldBeGeneratedForTheWorkOrder() {
        Response response = null;
        for (int i = 0; i < 15; i++) {
            response = given()
                    .header("Authorization", context.getAuthorization())
                    .param("externalId", context.getWorkOrderId())
                    .get("/v1/notifications")
                    .then()
                    .extract()
                    .response();

            if (response.statusCode() == 200) {
                return;
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
    }

    @Quando("o cliente aprova o orçamento e a execução é iniciada")
    public void theCustomerApprovesTheEstimateAndExecutionBegins() {
        updateWorkOrderStatus(WorkOrderStatus.EXECUTING);
    }

    @Quando("o serviço solicitado é marcado como concluído")
    public void theRequestedServiceIsMarkedAsCompleted() {
        var requestBody = WorkOrderDto.PatchRequest.builder()
                .finishedServiceId(UUID.fromString(context.getServiceId()))
                .build();

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .pathParam("workOrderId", context.getWorkOrderId())
                .patch("/v1/work-orders/{workOrderId}")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("estimatedServices[0].finishedAt")).isNotBlank();
        context.setLastResponse(response);
    }

    @E("a ordem de serviço é finalizada")
    public void theWorkOrderIsFinalized() {
        updateWorkOrderStatus(WorkOrderStatus.FINISHED);
    }

    @Quando("o veículo é liberado para o cliente")
    public void theVehicleIsReleasedToTheCustomer() {
        updateWorkOrderStatus(WorkOrderStatus.RELEASED);
    }

    @Quando("a rotina de tempo médio de execução é acionada")
    public void theAverageExecutionTimeRoutineIsTriggered() {
        var response = given()
                .header("Authorization", context.getAuthorization())
                .get("/v1/services/calculateAverageTime")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Entao("o tempo médio do serviço deve ser calculado e persistido")
    public void theServiceAverageTimeShouldBeCalculatedAndPersisted() {
        var response = given()
                .header("Authorization", context.getAuthorization())
                .pathParam("serviceId", context.getServiceId())
                .get("/v1/services/{serviceId}")
                .then()
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getLong("averageTimeInMinutes")).isNotNull();
    }

    private void updateWorkOrderStatus(WorkOrderStatus status) {
        try {
            log.info("Waiting 3 seconds in Cucumber before transitioning work order to status: {}", status);
            sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting before status transition", e);
        }

        var requestBody = WorkOrderDto.PatchRequest.builder()
                .status(status)
                .build();

        var response = given()
                .header("Authorization", context.getAuthorization())
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .pathParam("workOrderId", context.getWorkOrderId())
                .patch("/v1/work-orders/{workOrderId}")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
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
