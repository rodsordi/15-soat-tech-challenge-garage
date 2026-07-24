package br.com.fiap.garage.iandt;

import br.com.fiap.garage.GarageIntegrationTest;
import br.com.fiap.garage.application.GarageApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static br.com.fiap.garage.application.v1.dto.factory.VehicleDtoFactory.create_VehicleDto_Request;
import static br.com.fiap.garage.iandt.VehicleCreationTest.createVehicle;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
public class VehicleSearchTest extends GarageIntegrationTest {

    @DisplayName("When finding an vehicle by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a valid vehicle id, in scenario with saved vehicle")
            @Test
            void test1() {
                //Scenario
                var scenarioRequestBody = create_VehicleDto_Request().withAllFields();
                var scenarioResponse = createVehicle(authorization, json, scenarioRequestBody);
                //Given
                var vehicleId = scenarioResponse.jsonPath().getString("id");
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .pathParam("vehicleId", vehicleId)
                        .get("/v1/vehicles/{vehicleId}")
                        .then()
                        .log().all()
                        .extract()
                        .response();
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(200);
            }
        }
    }

    @DisplayName("When finding all vehicles")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given no query params, in scenario with saved vehicle")
            @Test
            void test1() {
                //Scenario
                var scenarioRequestBody = create_VehicleDto_Request().withAllFields();
                createVehicle(authorization, json, scenarioRequestBody);
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .get("/v1/vehicles")
                        .then()
                        .log().all()
                        .extract()
                        .response();
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(200);
            }
        }
    }
}
