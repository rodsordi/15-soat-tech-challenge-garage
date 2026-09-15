package br.com.fiap.commons.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path != null && path.contains("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String traceId = MDC.get("traceId");
            String spanId = MDC.get("spanId");
            boolean addedTrace = false;
            boolean addedSpan = false;

            if (traceId != null && !traceId.isBlank() && MDC.get("trace.id") == null) {
                MDC.put("trace.id", traceId);
                addedTrace = true;
            }
            if (spanId != null && !spanId.isBlank() && MDC.get("span.id") == null) {
                MDC.put("span.id", spanId);
                addedSpan = true;
            }

            try {
                log.info("HTTP {} {} completed with status {} in {}ms",
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        duration);
            } finally {
                if (addedTrace) {
                    MDC.remove("trace.id");
                }
                if (addedSpan) {
                    MDC.remove("span.id");
                }
            }
        }
    }
}
