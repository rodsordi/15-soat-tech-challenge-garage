package br.com.fiap.garage.application.adapter.repository;

import br.com.fiap.garage.domain.repository.CustomerRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface CustomerRepositoryExt extends CustomerRepository {

}
