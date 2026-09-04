package br.com.fiap.commons.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static com.google.gson.JsonParser.parseString;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.*;
import static org.springframework.util.StreamUtils.copyToString;

@Slf4j
@Configuration
public class LogInterceptor implements ClientHttpRequestInterceptor {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        var response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.debug("=== Request HTTP ===");
        log.debug("URI: {} {}", request.getMethod(), request.getURI());
        log.debug("Headers: {}", request.getHeaders());
        var contentType = request.getHeaders().getContentType();
        var requestBody = new String(body, UTF_8);
        log.debug("Body: {}", formatBody(contentType, requestBody));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.debug("=== Response HTTP ===");
        log.debug("Status: {}", response.getStatusCode());
        log.debug("Headers: {}", response.getHeaders());
        var responseBody = copyToString(response.getBody(), UTF_8);
        var contentType = response.getHeaders().getContentType();
        log.debug("Body: {}", formatBody(contentType, responseBody));
    }

    private String formatBody(MediaType contentType, String body) {
        try {
            if (isTextFormat(contentType)) {
                if (contentType.isCompatibleWith(APPLICATION_JSON)) {
                    var jsonElement = parseString(body);
                    return "\n" + gson.toJson(jsonElement);
                }
            } else {
                return String.format("[Omitting Binary content or Unknown. Type: %s]", contentType);
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return body;
    }

    private boolean isTextFormat(MediaType contentType) {
        if (contentType == null)
            return false;

        return contentType.isCompatibleWith(APPLICATION_JSON) ||
                contentType.isCompatibleWith(APPLICATION_XML) ||
                contentType.isCompatibleWith(APPLICATION_FORM_URLENCODED) ||
                "text".equalsIgnoreCase(contentType.getType()) ||
                contentType.getSubtype().endsWith("+json") ||
                contentType.getSubtype().endsWith("+xml");
    }
}