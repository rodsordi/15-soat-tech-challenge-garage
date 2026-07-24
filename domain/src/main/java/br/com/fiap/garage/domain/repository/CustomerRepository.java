package br.com.fiap.garage.domain.repository;

import br.com.fiap.garage.domain.entity.Customer;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@Order(1)
public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    Page<Customer> findAll(Specification<Customer> filter, Pageable pageable);

    Optional<Customer> findByDocument(String document);
}
