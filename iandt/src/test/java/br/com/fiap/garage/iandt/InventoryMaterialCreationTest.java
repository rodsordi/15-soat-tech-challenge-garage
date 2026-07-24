package br.com.fiap.garage.iandt;

import br.com.fiap.garage.GarageIntegrationTest;
import br.com.fiap.garage.application.GarageApplication;
import br.com.fiap.garage.application.v1.dto.InventoryMaterialDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.json.JsonMapper;

import static br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory.create_InventoryMaterialDto_Request;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
public class InventoryMaterialCreationTest extends GarageIntegrationTest {

    @DisplayName("When creating a new inventoryMaterial")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given an inventoryMaterial with all fields")
            @Test
            void test1() {
                //Given
                var requestBody = create_InventoryMaterialDto_Request()
                        .withAllFields();
                //When
                var response = createInventoryMaterial(authorization, json, requestBody);
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(201);
            }
        }
    }

    public static Response createInventoryMaterial(String authorization, JsonMapper json, InventoryMaterialDto.Request requestBody) {
        return given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .post("/v1/inventory-materials")
                .then()
                .log().all()
                .extract()
                .response();
    }
}
