package br.com.fiap.garage.e2e.context;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ScenarioTestContext {

    private String authorization = "Bearer e2e-integration-token";
    private String customerId;
    private String vehicleId;
    private String employeeId;
    private String materialId;
    private String serviceId;
    private String workOrderId;
    private Response lastResponse;

    public void reset() {
        customerId = null;
        vehicleId = null;
        employeeId = null;
        materialId = null;
        serviceId = null;
        workOrderId = null;
        lastResponse = null;
    }
}
