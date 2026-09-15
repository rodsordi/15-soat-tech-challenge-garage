package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.Customer;
import br.com.fiap.garage.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerCreationUseCase {

    private final CustomerRepository repository;

    @Transactional
    public Customer create(Customer customer) {
        var existingOpt = repository.findByDocument(customer.getDocument());
        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();
            if (customer.getId() == null || existing.getId().equals(customer.getId())) {
                existing.update(customer);
                return repository.save(existing);
            }
            repository.updateIdentity(existing.getId(), customer.getId(), customer.getName(), customer.getEmail());
            return repository.findById(customer.getId())
                    .orElse(customer);
        }
        return repository.save(customer);
    }
}
