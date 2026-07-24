package br.com.fiap.garage.iandt.use_case;

import br.com.fiap.garage.GarageIntegrationTest;
import br.com.fiap.garage.application.GarageApplication;
import br.com.fiap.garage.application.v1.dto.WorkOrderDto;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory.create_WorkOrderDto_Request;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.*;
import static br.com.fiap.garage.iandt.WorkOrderCreationTest.createWorkOrder;
import static br.com.fiap.garage.iandt.WorkOrderUpdateTest.updateWorkOrder;
import static io.restassured.RestAssured.given;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("int_test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = GarageApplication.class)
@Testcontainers
public class CompleteWorkOrderCreationUseCaseTest extends GarageIntegrationTest {

    @DisplayName("When executing the complete work flow")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given an workOrder with all fields")
            @Test
            void test1() {
                Response response;

                System.out.println("\n====== Creating a new work order ====\n");
                response = consumePostWorkOrders();
                var workOrderId = response.jsonPath()
                        .getString("id");

                System.out.println("\n====== Sending to the mechanic to diagnose the problems of the customer's vehicle ======\n");
                consumePatchWorkOrders_updateStatus(workOrderId, DIAGNOSING);

                System.out.println("\n====== Finished the diagnose, waiting for the customer's approval ======\n");
                consumePatchWorkOrders_updateStatus(workOrderId, WAITING_FOR_APPROVAL);

                System.out.println("\n====== Checking the e-mail notification informing the estimated service ======\n");
                consumeGetNotifications(workOrderId);

                System.out.println("\n====== Customer approval in website simulation ======\n");
                response = consumePatchWorkOrders_updateStatus(workOrderId, EXECUTING);
                var serviceId = response.jsonPath()
                        .getString("estimatedServices[0].id");

                System.out.println("\n====== Finishing the requested service ======\n");
                consumePatchWorkOrders_finishService(workOrderId, serviceId);

                System.out.println("\n====== Finished the work order ======\n");
                consumePatchWorkOrders_updateStatus(workOrderId, FINISHED);

                System.out.println("\n====== Released the vehicle to the customer ======\n");
                consumePatchWorkOrders_updateStatus(workOrderId, RELEASED);

                System.out.println("\n====== Simulate routine started of Services avg time calculation  ======\n");
                consumeGetServices_calculateAverageTime();

                System.out.println("\n====== Query services average time ======\n");
                consumeGetServices_assertAverageTime(serviceId);
            }
        }
    }

    private Response consumePostWorkOrders() {
        //Given
        var requestBody = create_WorkOrderDto_Request()
                .valid();
        //When
        return createWorkOrder(authorization, json, requestBody);
    }

    private Response consumeGetNotifications(String workOrderId) {
        //When
        var response = given()
                .log().all()
                .header("Authorization", authorization)
                .param("externalId", workOrderId)
                .get("/v1/notifications")
                .then()
                .log().all()
                .extract()
                .response();
        //Then
        assertThat(response.statusCode())
                .isEqualTo(200);
        return response;
    }

    private Response consumePatchWorkOrders_finishService(String workOrderId, String serviceId) {
        //Given
        var requestBody = WorkOrderDto.PatchRequest.builder()
                .finishedServiceId(UUID.fromString(serviceId))
                .build();
        //When
        var response = updateWorkOrder(authorization, json, workOrderId, requestBody);
        //Then
        assertThat(response.jsonPath().getString("estimatedServices[0].finishedAt"))
                .isNotBlank();
        return response;
    }

    private Response consumePatchWorkOrders_updateStatus(String workOrderId, WorkOrderStatus status) {
        //Given
        var requestBody = WorkOrderDto.PatchRequest.builder()
                .status(status)
                .build();
        //When
        var response = updateWorkOrder(authorization, json, workOrderId, requestBody);
        //Then
        assertThat(response.jsonPath().getString("status"))
                .isEqualTo(status.name());

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private Response consumeGetServices_calculateAverageTime() {
        //When
        var response = given()
                .log().all()
                .header("Authorization", authorization)
                .get("/v1/services/calculateAverageTime")
                .then()
                .log().all()
                .extract()
                .response();
        //Then
        assertThat(response.statusCode())
                .isEqualTo(204);
        return response;
    }

    private Response consumeGetServices_assertAverageTime(String serviceId) {
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
        assertThat(response.jsonPath()
                .getLong("averageTimeInMinutes")).isZero();
        return response;
    }
}