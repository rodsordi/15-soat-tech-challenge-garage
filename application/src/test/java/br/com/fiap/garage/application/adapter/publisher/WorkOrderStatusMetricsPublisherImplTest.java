package br.com.fiap.garage.application.adapter.publisher;

import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class WorkOrderStatusMetricsPublisherImplTest {

    private SimpleMeterRegistry meterRegistry;
    private WorkOrderStatusMetricsPublisherImpl publisher;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        publisher = new WorkOrderStatusMetricsPublisherImpl(meterRegistry);
    }

    @DisplayName("Should record duration metric for valid status and duration")
    @Test
    void shouldRecordStatusDuration() {
        // Given
        var status = WorkOrderStatus.DIAGNOSING;
        var duration = Duration.ofMinutes(15);

        // When
        publisher.recordStatusDuration(status, duration);

        // Then
        var timer = meterRegistry.find("garage.workorder.status.duration")
                .tag("status", "DIAGNOSING")
                .timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1L);
        assertThat(timer.totalTime(java.util.concurrent.TimeUnit.MINUTES)).isEqualTo(15.0);
    }

    @DisplayName("Should not record metric when duration is null or negative")
    @Test
    void shouldNotRecordWhenDurationInvalid() {
        publisher.recordStatusDuration(WorkOrderStatus.EXECUTING, null);
        publisher.recordStatusDuration(WorkOrderStatus.EXECUTING, Duration.ofMinutes(-5));
        publisher.recordStatusDuration(null, Duration.ofMinutes(10));

        var timer = meterRegistry.find("garage.workorder.status.duration").timer();
        assertThat(timer).isNull();
    }
}
