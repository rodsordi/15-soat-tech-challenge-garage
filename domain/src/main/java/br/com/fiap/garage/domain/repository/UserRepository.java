package br.com.fiap.garage.domain.repository;

import br.com.fiap.garage.domain.entity.User;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@Order(0)
public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByEmail(String username);
}
