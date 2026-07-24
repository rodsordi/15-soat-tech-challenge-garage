package br.com.fiap.garage.iandt;

import br.com.fiap.garage.GarageIntegrationTest;
import br.com.fiap.garage.application.GarageApplication;
import br.com.fiap.garage.application.v1.dto.WorkOrderDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;
import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory.create_EmployeeDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory.create_InventoryMaterialDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.ServiceDtoFactory.create_ServiceDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.VehicleDtoFactory.create_VehicleDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory.create_WorkOrderDto_Request;
import static br.com.fiap.garage.iandt.EmployeeCreationTest.createEmployee;
import static br.com.fiap.garage.iandt.InventoryMaterialCreationTest.createInventoryMaterial;
import static br.com.fiap.garage.iandt.ServiceCreationTest.createService;
import static br.com.fiap.garage.iandt.VehicleCreationTest.createVehicle;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
public class WorkOrderCreationTest extends GarageIntegrationTest {

    @DisplayName("When creating a new workOrder")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given an workOrder with all fields")
            @Test
            void test1() {
                //Given
                var requestBody = create_WorkOrderDto_Request().valid();
                //When
                var response = createWorkOrder(authorization, json, requestBody);
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(201);
            }
        }
    }

    public static Response createWorkOrder(String authorization, JsonMapper json, WorkOrderDto.Request requestBody) {
        var vehicleRequestBody = create_VehicleDto_Request().valid();
        var vehicleResponse = createVehicle(authorization, json, vehicleRequestBody);
        var vehicleId = vehicleResponse.body().jsonPath().getString("id");
        setField(requestBody, "vehicleId", UUID.fromString(vehicleId));

        var employeeRequestBody = create_EmployeeDto_Request().valid();
        var employeeResponse = createEmployee(json, employeeRequestBody);
        var employeeId = employeeResponse.body().jsonPath().getString("id");
        setField(requestBody, "employeeId", UUID.fromString(employeeId));

        var inventoryMaterialRequestBody = create_InventoryMaterialDto_Request().valid();
        var inventoryMaterialResponse = createInventoryMaterial(authorization, json, inventoryMaterialRequestBody);
        var materialId = inventoryMaterialResponse.body().jsonPath().getString("id");
        var materialsIds = Set.of(UUID.fromString(materialId));

        var serviceRequestBody = create_ServiceDto_Request().valid();
        setField(serviceRequestBody, "materialsIds", materialsIds);
        var serviceResponse = createService(authorization, json, serviceRequestBody);
        var serviceId = serviceResponse.body().jsonPath().getString("id");
        var servicesIds = Set.of(UUID.fromString(serviceId));
        setField(requestBody, "servicesIds", servicesIds);

        var response = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .post("/v1/work-orders")
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(response.statusCode())
                .isEqualTo(201);
        return response;
    }
}