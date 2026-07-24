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

import static br.com.fiap.garage.application.v1.dto.factory.ServiceDtoFactory.create_ServiceDto_Request;
import static br.com.fiap.garage.iandt.ServiceCreationTest.createService;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
public class ServiceSearchTest extends GarageIntegrationTest {

    @DisplayName("When finding an service by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a valid service id, in scenario with saved service")
            @Test
            void test1() {
                //Scenario
                var scenarioRequestBody = create_ServiceDto_Request().withAllFields();
                var scenarioResponse = createService(authorization, json, scenarioRequestBody);
                //Given
                var serviceId = scenarioResponse.jsonPath().getString("id");
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .pathParam("serviceId", serviceId)
                        .get("/v1/services/{serviceId}")
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

    @DisplayName("When finding all services")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given no query params, in scenario with saved service")
            @Test
            void test1() {
                //Scenario
                var scenarioRequestBody = create_ServiceDto_Request().withAllFields();
                createService(authorization, json, scenarioRequestBody);
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .get("/v1/services")
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
