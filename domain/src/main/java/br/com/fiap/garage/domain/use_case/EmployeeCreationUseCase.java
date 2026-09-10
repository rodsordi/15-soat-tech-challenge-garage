package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.Employee;
import br.com.fiap.garage.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeCreationUseCase {

    private final EmployeeRepository repository;

    public Employee create(Employee employee) {
        return repository.save(employee);
    }
}
