package br.com.fiap.garage.iandt;

import br.com.fiap.garage.GarageIntegrationTest;
import br.com.fiap.garage.application.GarageApplication;
import br.com.fiap.garage.application.v1.dto.ServiceDto;
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

import static br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory.create_InventoryMaterialDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.ServiceDtoFactory.create_ServiceDto_Request;
import static br.com.fiap.garage.iandt.InventoryMaterialCreationTest.createInventoryMaterial;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
class ServiceCreationTest extends GarageIntegrationTest {

    @DisplayName("When creating a new service")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a valid service, in scenario with a registered inventory material")
            @Test
            void test1() {
                //Given
                var requestBody = create_ServiceDto_Request()
                        .withAllFields();
                //When
                var response = createService(authorization, json, requestBody);
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(201);
            }
        }
    }

    public static Response createService(String authorization, JsonMapper json, ServiceDto.Request requestBody) {
        var registeredInventoryMaterial = create_InventoryMaterialDto_Request().withAllFields();
        var scenarioResponse = createInventoryMaterial(authorization, json, registeredInventoryMaterial);
        var materialId = UUID.fromString(scenarioResponse.jsonPath().getString("id"));
        setField(requestBody, "materialsIds", Set.of(materialId));
        return given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .post("/v1/services")
                .then()
                .log().all()
                .extract()
                .response();
    }
}
