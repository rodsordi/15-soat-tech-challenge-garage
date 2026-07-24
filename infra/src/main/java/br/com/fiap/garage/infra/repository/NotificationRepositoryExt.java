package br.com.fiap.garage.infra.repository;

import br.com.fiap.garage.domain.repository.NotificationRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface NotificationRepositoryExt extends NotificationRepository {

}
