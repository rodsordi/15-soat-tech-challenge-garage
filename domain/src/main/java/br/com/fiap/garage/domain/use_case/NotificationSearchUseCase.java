package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Notification;
import br.com.fiap.garage.domain.filter.NotificationFilter;
import br.com.fiap.garage.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationSearchUseCase {

    private final NotificationRepository repository;

    public Notification findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Notification.class, "id", id));
    }

    public Page<Notification> findAll(NotificationFilter filter) {
        var foundNotifications = repository.findAll(filter, filter.buildPageRequest());
        if (foundNotifications == null || foundNotifications.isEmpty())
            throw new ResourceNotFoundException(Notification.class);
        return foundNotifications;
    }
}
