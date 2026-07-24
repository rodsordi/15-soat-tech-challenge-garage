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

import static br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory.create_EmployeeDto_Request;
import static br.com.fiap.garage.iandt.EmployeeCreationTest.createEmployee;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
class EmployeeSearchTest extends GarageIntegrationTest {

    @DisplayName("When finding an employee by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a valid employee id, in scenario with saved employee")
            @Test
            void test1() {
                //Scenario
                var scenarioRequestBody = create_EmployeeDto_Request().withAllFields();
                var scenarioResponse = createEmployee(json, scenarioRequestBody);
                //Given
                var employeeId = scenarioResponse.jsonPath().getString("id");
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .pathParam("employeeId", employeeId)
                        .get("/v1/employees/{employeeId}")
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

    @DisplayName("When finding all employees")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given no query params, in scenario with saved employee")
            @Test
            void test1() {
                //Scenario
                var scenarioRequestBody = create_EmployeeDto_Request().withAllFields();
                createEmployee(json, scenarioRequestBody);
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .get("/v1/employees")
                        .then()
                        .log().all()
                        .extract()
                        .response();
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(200);
            }

            @DisplayName("Given a valid cpf query param, in scenario with saved employee")
            @Test
            void test2() {
                //Scenario
                var scenarioRequestBody = create_EmployeeDto_Request().withAllFields();
                createEmployee(json, scenarioRequestBody);
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .param("cpf", "90443471010")
                        .get("/v1/employees")
                        .then()
                        .log().all()
                        .extract()
                        .response();
                //Then
                Assertions.assertThat(response.statusCode())
                        .isEqualTo(200);
                Assertions.assertThat(response.body().jsonPath().getList("content"))
                        .hasSize(1);
                Assertions.assertThat(response.body().jsonPath().getString("content.[0].cpf"))
                        .isEqualTo("904.434.710-10");
            }
        }
    }
}