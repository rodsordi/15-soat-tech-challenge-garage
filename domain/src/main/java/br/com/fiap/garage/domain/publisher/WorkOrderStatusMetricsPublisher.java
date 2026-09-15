package br.com.fiap.garage.domain.publisher;

import br.com.fiap.garage.domain.enums.WorkOrderStatus;

import java.time.Duration;

public interface WorkOrderStatusMetricsPublisher {

    void recordStatusDuration(WorkOrderStatus status, Duration duration);
}
