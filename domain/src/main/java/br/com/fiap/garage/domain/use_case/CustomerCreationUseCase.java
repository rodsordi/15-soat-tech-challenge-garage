package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.Customer;
import br.com.fiap.garage.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerCreationUseCase {

    private final CustomerRepository repository;

    private final PasswordEncoder passwordEncoder;

    public Customer create(Customer customer) {
        customer.encodePassword(passwordEncoder);
        return repository.save(customer);
    }
}
