package br.com.fiap.garage.iandt;

import br.com.fiap.garage.GarageIntegrationTest;
import br.com.fiap.garage.application.GarageApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static br.com.fiap.garage.application.v1.dto.factory.NotificationDtoFactory.create_NotificationDto_Request;
import static br.com.fiap.garage.iandt.NotificationCreationTest.createNotification;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
public class NotificationSearchTest extends GarageIntegrationTest {

    @DisplayName("When finding an notification by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a valid notification id, in scenario with registered notification")
            @Test
            void test1() {
                //Scenario
                var registeredNotification = create_NotificationDto_Request().withAllFields();
                var scenarioResponse = createNotification(authorization, json, registeredNotification);
                //Given
                var notificationId = scenarioResponse.jsonPath().getString("id");
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .pathParam("notificationId", notificationId)
                        .get("/v1/notifications/{notificationId}")
                        .then()
                        .log().all()
                        .extract()
                        .response();
                //Then
                Assertions.assertThat(response.statusCode())
                        .isEqualTo(200);
            }
        }
    }
    
    @DisplayName("When finding all inventory materials")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given no query params, in scenario with saved inventory materials")
            @Test
            void test1() {
                //Scenario
                var scenarioRequestBody = create_NotificationDto_Request().withAllFields();
                createNotification(authorization, json, scenarioRequestBody);
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .get("/v1/notifications")
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
