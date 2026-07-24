package br.com.fiap.garage.application.v1.listener;

import br.com.fiap.garage.application.v1.mapper.NotificationMsgMapper;
import br.com.fiap.garage.application.v1.msg.NotificationMsg;
import br.com.fiap.garage.domain.use_case.NotificationCreationUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.mapstruct.factory.Mappers.getMapper;

@RequiredArgsConstructor
@Component
public class NotificationListener {

    private static final NotificationMsgMapper MAPPER = getMapper(NotificationMsgMapper.class);

    private final NotificationCreationUseCase notificationCreationUseCase;

    @SqsListener(value = "${message.notification-creation.queue}")
    public void listenNotificationCreation(NotificationMsg notificationMsg) {
        var notification = MAPPER.convert(notificationMsg);
        notificationCreationUseCase.create(notification);
    }
}
