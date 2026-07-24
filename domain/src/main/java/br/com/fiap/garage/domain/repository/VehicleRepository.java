package br.com.fiap.garage.domain.repository;

import br.com.fiap.garage.domain.entity.Vehicle;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

@Order(2)
public interface VehicleRepository extends CrudRepository<Vehicle, UUID> {

    Page<Vehicle> findAll(Specification<Vehicle> filter, Pageable pageable);
}
