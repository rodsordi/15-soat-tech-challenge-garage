package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.Notification;
import br.com.fiap.garage.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationCreationUseCase {

    private final NotificationRepository repository;

    @Value("${email.garage-management-email-recipient}")
    private String garageManagementEmailRecipient;

    public Notification create(Notification notification) {
        notification.initEmailBcc(garageManagementEmailRecipient);
        return repository.save(notification);
    }
}
