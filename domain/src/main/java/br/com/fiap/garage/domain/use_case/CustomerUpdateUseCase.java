package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Customer;
import br.com.fiap.garage.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerUpdateUseCase {

    private final CustomerRepository repository;

    public Customer update(UUID id, Customer customer) {
        var foundCustomer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Customer.class, "id", id));
        foundCustomer.update(customer);
        return repository.save(foundCustomer);
    }
}
