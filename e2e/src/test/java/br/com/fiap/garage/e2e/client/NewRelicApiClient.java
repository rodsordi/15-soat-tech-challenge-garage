package br.com.fiap.garage.e2e.client;

import br.com.fiap.garage.e2e.config.E2eConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class NewRelicApiClient {

    private static final Logger log = LoggerFactory.getLogger(NewRelicApiClient.class);
    private static final String GRAPHQL_URL = "https://api.newrelic.com/graphql";

    private final HttpClient httpClient;
    private final JsonMapper json;

    public NewRelicApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.json = JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    public record NewRelicLogRecord(long timestamp, String message, String traceId, String spanId) {}

    public Optional<NewRelicLogRecord> findLog(String method, String uri, int statusCode, String entityId, Duration timeout) {
        var accountId = E2eConfig.getNewRelicAccountId();
        var apiKey = E2eConfig.getNewRelicApiKey();

        var pathKeyword = extractPathKeyword(uri);

        var queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT timestamp, message, ")
                .append("capture(message, r'.*\"traceId\":\"(?P<TraceId>[a-f0-9]+)\".*') AS 'TraceId', ")
                .append("capture(message, r'.*\"spanId\":\"(?P<SpanId>[a-f0-9]+)\".*') AS 'SpanId' ")
                .append("FROM Log WHERE (kubernetes.container_name = 'api-garage' OR container_name = 'api-garage') ")
                .append("AND message LIKE '%").append(method).append("%' ")
                .append("AND message LIKE '%").append(pathKeyword).append("%' ")
                .append("AND message LIKE '%").append(statusCode).append("%' ");

        if (entityId != null && !entityId.isBlank() && uri != null && uri.contains("/" + entityId)) {
            queryBuilder.append("AND message LIKE '%/").append(entityId).append("%' ");
        }

        queryBuilder.append("SINCE 5 minutes ago LIMIT 1");
        var nrql = queryBuilder.toString();

        log.info("Polling New Relic for log matching: method={}, pathKeyword={}, status={}, entityId={}",
                method, pathKeyword, statusCode, entityId);

        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            try {
                var recordOpt = executeNrqlLogQuery(accountId, apiKey, nrql);
                if (recordOpt.isPresent()) {
                    return recordOpt;
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("Transient error while querying New Relic: {}", e.getMessage());
            }
        }
        return Optional.empty();
    }

    public boolean verifyWorkOrderStatusDurationMetric(String status, Duration timeout) {
        var accountId = E2eConfig.getNewRelicAccountId();
        var apiKey = E2eConfig.getNewRelicApiKey();

        var nrql = String.format("SELECT count(*) FROM Metric WHERE metricName LIKE '%%garage.workorder.status.duration%%' AND status = '%s' SINCE 15 minutes ago", status);
        log.info("Polling New Relic for metric: status={}", status);

        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            try {
                long count = executeNrqlCountQuery(accountId, apiKey, nrql);
                if (count > 0) {
                    return true;
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.warn("Transient error while querying New Relic metrics: {}", e.getMessage());
            }
        }
        return false;
    }

    private Optional<NewRelicLogRecord> executeNrqlLogQuery(String accountId, String apiKey, String nrql) throws Exception {
        var graphQLBody = buildGraphQLQuery(accountId, nrql);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(GRAPHQL_URL))
                .header("API-Key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(graphQLBody))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            log.warn("New Relic GraphQL returned HTTP {}: {}", response.statusCode(), response.body());
            return Optional.empty();
        }

        JsonNode root = json.readTree(response.body());
        JsonNode results = root.path("data").path("actor").path("account").path("nrql").path("results");
        if (results.isArray() && !results.isEmpty()) {
            JsonNode item = results.get(0);
            String traceId = item.path("TraceId").asText(null);
            String spanId = item.path("SpanId").asText(null);
            String message = item.path("message").asText(null);
            long timestamp = item.path("timestamp").asLong(0);

            if (traceId != null && spanId != null) {
                return Optional.of(new NewRelicLogRecord(timestamp, message, traceId, spanId));
            }
        }
        return Optional.empty();
    }

    private long executeNrqlCountQuery(String accountId, String apiKey, String nrql) throws Exception {
        var graphQLBody = buildGraphQLQuery(accountId, nrql);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(GRAPHQL_URL))
                .header("API-Key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(graphQLBody))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return 0;
        }

        JsonNode root = json.readTree(response.body());
        JsonNode results = root.path("data").path("actor").path("account").path("nrql").path("results");
        if (results.isArray() && !results.isEmpty()) {
            return results.get(0).path("count").asLong(0);
        }
        return 0;
    }

    private String buildGraphQLQuery(String accountId, String nrql) throws Exception {
        var escapedNrql = nrql.replace("\"", "\\\"");
        var query = String.format("{ actor { account(id: %s) { nrql(query: \"%s\") { results } } } }", accountId, escapedNrql);
        return json.writeValueAsString(Map.of("query", query));
    }

    private String extractPathKeyword(String uri) {
        if (uri == null || uri.isBlank()) {
            return "v1";
        }
        try {
            var path = uri.contains("://") ? URI.create(uri).getPath() : uri;
            if (path.contains("/customers")) return "customers";
            if (path.contains("/employees")) return "employees";
            if (path.contains("/vehicles")) return "vehicles";
            if (path.contains("/inventory-materials")) return "inventory-materials";
            if (path.contains("/materials")) return "materials";
            if (path.contains("/services")) return "services";
            if (path.contains("/work-orders")) return "work-orders";
            if (path.contains("/notifications")) return "notifications";
            return "v1";
        } catch (Exception e) {
            return "v1";
        }
    }
}