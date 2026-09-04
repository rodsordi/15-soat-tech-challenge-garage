package br.com.fiap.garage.application.adapter.publisher;

import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.publisher.NotifyCustomerForApprovalPublisher;
import br.com.fiap.garage.application.adapter.mapper.NotificationEvtMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotifyCustomerForApprovalPublisherImpl implements NotifyCustomerForApprovalPublisher {

    private final NotificationEvtMapper mapper;

    private final SnsTemplate snsTemplate;

    @Value("${message.notification-creation.topic}")
    private String queueName;

    @Override
    public void notify(WorkOrder workOrder) {
        var notificationEvt = mapper.convert(workOrder);
        snsTemplate.convertAndSend(queueName, notificationEvt);
    }
}
