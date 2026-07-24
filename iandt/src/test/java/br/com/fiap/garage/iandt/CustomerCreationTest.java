package br.com.fiap.garage.iandt;

import br.com.fiap.garage.GarageIntegrationTest;
import br.com.fiap.garage.application.GarageApplication;
import br.com.fiap.garage.application.v1.dto.CustomerDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.json.JsonMapper;

import static br.com.fiap.garage.application.v1.dto.factory.CustomerDtoFactory.create_CustomerDto_Request;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
public class CustomerCreationTest extends GarageIntegrationTest {

    @DisplayName("When creating a new customer")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given an customer with all fields")
            @Test
            void test1() {
                //Given
                var requestBody = create_CustomerDto_Request()
                        .withAllFields();
                //When
                var response = createCustomer(authorization, json, requestBody);
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(201);
            }
        }
    }

    public static Response createCustomer(String authorization, JsonMapper json, CustomerDto.Request requestBody) {
        return given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .post("/v1/customers")
                .then()
                .log().all()
                .extract()
                .response();
    }
}
