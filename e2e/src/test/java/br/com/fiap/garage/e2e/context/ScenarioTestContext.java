package br.com.fiap.garage.e2e.context;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ScenarioTestContext {

    private String authorization = br.com.fiap.garage.e2e.config.E2eConfig.getAuthToken();
    private String customerId;
    private String vehicleId;
    private String employeeId;
    private String materialId;
    private String serviceId;
    private String workOrderId;
    private Response lastResponse;
    private final java.util.List<RecordedRequest> recordedRequests = new java.util.ArrayList<>();

    public void recordRequest(String method, String uri, int statusCode) {
        recordedRequests.add(new RecordedRequest(method, uri, statusCode));
    }

    public RecordedRequest getLastRecordedRequest() {
        return recordedRequests.isEmpty() ? null : recordedRequests.get(recordedRequests.size() - 1);
    }

    public RecordedRequest getLastApiGarageRequest() {
        for (int i = recordedRequests.size() - 1; i >= 0; i--) {
            var req = recordedRequests.get(i);
            if (req.uri() != null && (req.uri().contains("/v1/") || req.uri().contains("/api/"))) {
                return req;
            }
        }
        return null;
    }

    public String resolveRelevantEntityId() {
        if (workOrderId != null) return workOrderId;
        if (customerId != null) return customerId;
        if (vehicleId != null) return vehicleId;
        if (employeeId != null) return employeeId;
        if (materialId != null) return materialId;
        if (serviceId != null) return serviceId;
        return null;
    }

    public void reset() {
        authorization = br.com.fiap.garage.e2e.config.E2eConfig.getAuthToken();
        customerId = null;
        vehicleId = null;
        employeeId = null;
        materialId = null;
        serviceId = null;
        workOrderId = null;
        lastResponse = null;
        recordedRequests.clear();
    }

    public record RecordedRequest(String method, String uri, int statusCode) {}
}
