package br.com.fiap.garage.iandt;

import br.com.fiap.garage.GarageIntegrationTest;
import br.com.fiap.garage.application.GarageApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static br.com.fiap.garage.application.v1.dto.factory.CustomerDtoFactory.create_CustomerDto_Request;
import static br.com.fiap.garage.iandt.CustomerCreationTest.createCustomer;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
public class CustomerSearchTest extends GarageIntegrationTest {

    @DisplayName("When finding an customer by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a valid customer id, in scenario with registered customer")
            @Test
            void test1() {
                //Scenario
                var registeredCustomer = create_CustomerDto_Request().withAllFields();
                var scenarioResponse = createCustomer(authorization, json, registeredCustomer);
                //Given
                var customerId = scenarioResponse.jsonPath().getString("id");
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .pathParam("customerId", customerId)
                        .get("/v1/customers/{customerId}")
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

    @DisplayName("When finding all customers")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given no query params, in scenario with registered customer")
            @Test
            void test1() {
                //Scenario
                var scenarioRequestBody = create_CustomerDto_Request().withAllFields();
                createCustomer(authorization, json, scenarioRequestBody);
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .get("/v1/customers")
                        .then()
                        .log().all()
                        .extract()
                        .response();
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(200);
            }

            @DisplayName("Given a customer with {document}, and {documentFilter} query param, in scenario with registered customer")
            @CsvSource(value = {
                    "54.662.770/0001-29 | 54662770000129 | 54.662.770/0001-29",
                    "33268627000187     | 33268627000187 | 33.268.627/0001-87",
                    "574.425.940-69     | 57442594069    | 574.425.940-69",
                    "48424855078        | 48424855078    | 484.248.550-78",
            }, delimiterString = "|")
            @ParameterizedTest
            void test(String document, String documentFilter, String expected) {
                //Scenario
                var scenarioRequestBody = create_CustomerDto_Request().withAllFields();
                setField(scenarioRequestBody, "document", document);
                createCustomer(authorization, json, scenarioRequestBody);
                //When
                var response = given()
                        .log().all()
                        .header("Authorization", authorization)
                        .param("document", documentFilter)
                        .get("/v1/customers")
                        .then()
                        .log().all()
                        .extract()
                        .response();
                //Then
                assertThat(response.statusCode())
                        .isEqualTo(200);
                assertThat(response.body().jsonPath().getList("content"))
                        .hasSize(1);
                assertThat(response.body().jsonPath().getString("content.[0].document"))
                        .isEqualTo(expected);
            }
        }
    }
}
