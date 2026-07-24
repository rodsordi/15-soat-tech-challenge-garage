package br.com.fiap.garage.domain.repository;

import br.com.fiap.garage.domain.entity.Notification;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

@Order(0)
public interface NotificationRepository extends CrudRepository<Notification, UUID> {

    Page<Notification> findAll(Specification<Notification> filter, Pageable pageable);
}
