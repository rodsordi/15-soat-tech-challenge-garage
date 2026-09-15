package br.com.fiap.garage.e2e.steps;

import br.com.fiap.garage.e2e.client.NewRelicApiClient;
import br.com.fiap.garage.e2e.config.E2eConfig;
import br.com.fiap.garage.e2e.context.ScenarioTestContext;
import io.cucumber.java.pt.E;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class ObservabilitySteps {

    private static final Logger log = LoggerFactory.getLogger(ObservabilitySteps.class);

    private final ScenarioTestContext context;
    private final NewRelicApiClient newRelicApiClient = new NewRelicApiClient();

    public ObservabilitySteps(ScenarioTestContext context) {
        this.context = context;
    }

    @E("os dados de telemetria da operação devem ser validados no New Relic via API")
    @E("os dados de telemetria da requisição devem ser validados no New Relic via API")
    public void theOperationTelemetryMustBeValidatedInNewRelicViaApi() {
        if (!E2eConfig.isNewRelicValidationEnabled()) {
            log.info("New Relic validation is disabled via configuration (newrelic.validation.enabled=false). Skipping.");
            return;
        }

        var lastRequest = context.getLastApiGarageRequest();
        if (lastRequest == null) {
            log.info("No API Garage request was executed in this scenario (e.g. direct Lambda-only test). Skipping New Relic log check.");
            return;
        }

        var timeout = Duration.ofSeconds(E2eConfig.getNewRelicValidationTimeoutSeconds());
        var entityId = context.resolveRelevantEntityId();

        var logRecordOpt = newRelicApiClient.findLog(lastRequest.method(), lastRequest.uri(), lastRequest.statusCode(), entityId, timeout);

        assertThat(logRecordOpt)
                .withFailMessage("Expected to find log in New Relic for %s %s with status %d within %ds, but none was found",
                        lastRequest.method(), lastRequest.uri(), lastRequest.statusCode(), timeout.toSeconds())
                .isPresent();

        var record = logRecordOpt.get();
        log.info("Found correlated log in New Relic: traceId={}, spanId={}, msg={}", record.traceId(), record.spanId(), record.message());

        assertThat(record.traceId())
                .withFailMessage("TraceId in New Relic log should not be null or blank")
                .isNotBlank()
                .hasSize(32);

        assertThat(record.spanId())
                .withFailMessage("SpanId in New Relic log should not be null or blank")
                .isNotBlank()
                .hasSize(16);
    }

    @E("as métricas de tempo de execução por status devem estar disponíveis no New Relic via API")
    public void theWorkOrderStatusExecutionMetricsMustBeAvailableInNewRelicViaApi() {
        if (!E2eConfig.isNewRelicValidationEnabled()) {
            log.info("New Relic validation is disabled via configuration. Skipping metric check.");
            return;
        }

        var timeout = Duration.ofSeconds(E2eConfig.getNewRelicMetricTimeoutSeconds());

        boolean diagnosingMetricFound = newRelicApiClient.verifyWorkOrderStatusDurationMetric("DIAGNOSING", timeout);
        assertThat(diagnosingMetricFound)
                .withFailMessage("Expected metric 'garage.workorder.status.duration' with status='DIAGNOSING' in New Relic, but none was found within %ds", timeout.toSeconds())
                .isTrue();

        boolean executingMetricFound = newRelicApiClient.verifyWorkOrderStatusDurationMetric("EXECUTING", timeout);
        assertThat(executingMetricFound)
                .withFailMessage("Expected metric 'garage.workorder.status.duration' with status='EXECUTING' in New Relic, but none was found within %ds", timeout.toSeconds())
                .isTrue();

        log.info("Successfully validated work order status duration metrics in New Relic for DIAGNOSING and EXECUTING.");
    }
}