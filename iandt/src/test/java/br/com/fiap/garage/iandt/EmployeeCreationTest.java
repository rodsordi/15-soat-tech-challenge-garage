package br.com.fiap.garage.iandt;

import br.com.fiap.garage.GarageIntegrationTest;
import br.com.fiap.garage.application.GarageApplication;
import br.com.fiap.garage.application.v1.dto.EmployeeDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.json.JsonMapper;

import static br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory.create_EmployeeDto_Request;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
class EmployeeCreationTest extends GarageIntegrationTest {

    @DisplayName("When creating a new employee")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given an employee with all fields")
            @Test
            void test1() {
                //Given
                var scenarioRequestBody = create_EmployeeDto_Request()
                        .withAllFields();
                setField(scenarioRequestBody, "cpf", "907.322.300-80");
                //When
                var response = createEmployee(json, scenarioRequestBody);
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(201);
            }
        }
    }

    public static Response createEmployee(JsonMapper json, EmployeeDto.Request requestBody) {
        return given()
                .log().all()
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .post("/v1/employees")
                .then()
                .log().all()
                .extract()
                .response();
    }
}
