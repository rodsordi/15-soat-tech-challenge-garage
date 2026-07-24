package br.com.fiap.garage.domain.repository;

import br.com.fiap.garage.domain.entity.Material;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

@Order(0)
public interface MaterialRepository extends CrudRepository<Material, UUID> {

}
