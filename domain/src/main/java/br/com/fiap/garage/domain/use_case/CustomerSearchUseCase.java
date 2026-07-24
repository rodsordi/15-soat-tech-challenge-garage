package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Customer;
import br.com.fiap.garage.domain.filter.CustomerFilter;
import br.com.fiap.garage.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerSearchUseCase {

    private final CustomerRepository repository;

    public Customer findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Customer.class, "id", id));
    }

    public Page<Customer> findAll(CustomerFilter filter) {
        var foundCustomers = repository.findAll(filter, filter.buildPageRequest());
        if (foundCustomers == null || foundCustomers.isEmpty())
            throw new ResourceNotFoundException(Customer.class);
        return foundCustomers;
    }
}
