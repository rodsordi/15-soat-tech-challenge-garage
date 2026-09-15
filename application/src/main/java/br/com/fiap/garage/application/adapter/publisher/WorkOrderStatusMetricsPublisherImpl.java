package br.com.fiap.garage.application.adapter.publisher;

import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import br.com.fiap.garage.domain.publisher.WorkOrderStatusMetricsPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkOrderStatusMetricsPublisherImpl implements WorkOrderStatusMetricsPublisher {

    private final MeterRegistry meterRegistry;

    @Override
    public void recordStatusDuration(WorkOrderStatus status, Duration duration) {
        if (status != null && duration != null && !duration.isNegative()) {
            meterRegistry.timer("garage.workorder.status.duration", "status", status.name())
                    .record(duration);
            log.info("Recorded work order status duration metric: status={}, durationMs={}", status.name(), duration.toMillis());
        }
    }
}
