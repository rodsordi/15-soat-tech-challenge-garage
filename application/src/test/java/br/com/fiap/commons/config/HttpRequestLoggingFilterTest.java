package br.com.fiap.commons.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpRequestLoggingFilterTest {

    private final HttpRequestLoggingFilter filter = new HttpRequestLoggingFilter();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("Should skip filtering for actuator endpoints")
    void shouldSkipFilteringForActuatorEndpoints() {
        when(request.getRequestURI()).thenReturn("/api/actuator/health");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    @DisplayName("Should apply filtering for business endpoints")
    void shouldApplyFilteringForBusinessEndpoints() {
        when(request.getRequestURI()).thenReturn("/api/v1/work-orders");
        assertThat(filter.shouldNotFilter(request)).isFalse();
    }

    @Test
    @DisplayName("Should log request execution and populate trace MDC keys")
    void shouldLogRequestExecutionAndPopulateTraceMdcKeys() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/customers");
        when(response.getStatus()).thenReturn(201);

        MDC.put("traceId", "1234567890abcdef1234567890abcdef");
        MDC.put("spanId", "abcdef1234567890");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(MDC.get("trace.id")).isNull();
        assertThat(MDC.get("span.id")).isNull();
    }

    @Test
    @DisplayName("Should log and cleanup even when filter chain throws exception")
    void shouldLogAndCleanupWhenFilterChainThrowsException() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/vehicles");
        when(response.getStatus()).thenReturn(500);

        MDC.put("traceId", "1234567890abcdef1234567890abcdef");

        doThrow(new RuntimeException("Simulated error")).when(filterChain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Simulated error");

        assertThat(MDC.get("trace.id")).isNull();
    }
}
