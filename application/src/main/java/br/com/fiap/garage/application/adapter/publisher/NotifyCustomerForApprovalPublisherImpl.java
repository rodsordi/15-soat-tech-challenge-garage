package br.com.fiap.garage.application.adapter.publisher;

import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.publisher.NotifyCustomerForApprovalPublisher;
import br.com.fiap.garage.application.adapter.mapper.NotificationEvtMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotifyCustomerForApprovalPublisherImpl implements NotifyCustomerForApprovalPublisher {

    private final NotificationEvtMapper mapper;

    private final Optional<SnsTemplate> snsTemplate;

    @Value("${message.notification-creation.topic:api-garage_notification-creation_topic}")
    private String topicName;

    @Override
    public void notify(WorkOrder workOrder) {
        if (snsTemplate.isEmpty()) {
            log.warn("SNS messaging is disabled or SnsTemplate bean is not configured. Skipping notification for workOrder id: {}",
                    workOrder != null ? workOrder.getId() : "null");
            return;
        }

        try {
            var notificationEvt = mapper.convert(workOrder);
            snsTemplate.get().convertAndSend(topicName, notificationEvt);
            log.info("Successfully published notification event to SNS topic [{}] for workOrder id: {}",
                    topicName, workOrder != null ? workOrder.getId() : "null");
        } catch (Exception ex) {
            log.error("Failed to publish notification event to SNS topic [{}] for workOrder id: {}. Reason: {}",
                    topicName, workOrder != null ? workOrder.getId() : "null", ex.getMessage(), ex);
        }
    }
}
