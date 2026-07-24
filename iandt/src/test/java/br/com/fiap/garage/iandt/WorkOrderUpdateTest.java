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

import static br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory.create_WorkOrderDto_Request;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.DIAGNOSING;
import static br.com.fiap.garage.iandt.WorkOrderCreationTest.createWorkOrder;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.text.MessageFormat.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
public class WorkOrderUpdateTest extends GarageIntegrationTest {

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
                var workOrderId = response.jsonPath().getString("id");

                //Given
                var requestBody2 = WorkOrderDto.PatchRequest.builder()
                        .status(DIAGNOSING)
                        .build();
                //When
                var response2 = updateWorkOrder(authorization, json, workOrderId, requestBody2);
                //Then
                assertThat(response2.statusCode())
                        .isEqualTo(200);
                assertThat(response2.jsonPath().getString("status"))
                        .isEqualTo("DIAGNOSING");
            }
        }
    }

    public static Response updateWorkOrder(String authorization, JsonMapper json, String workOrderId, WorkOrderDto.PatchRequest requestBody) {
        var response = given()
                .log().all()
                .header("Authorization", authorization)
                .contentType(JSON)
                .body(json.writeValueAsString(requestBody))
                .patch(format("/v1/work-orders/{0}", workOrderId))
                .then()
                .log().all()
                .extract()
                .response();
        assertThat(response.statusCode())
                .isEqualTo(200);
        return response;
    }
}