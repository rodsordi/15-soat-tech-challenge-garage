package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Employee;
import br.com.fiap.garage.domain.filter.EmployeeFilter;
import br.com.fiap.garage.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeSearchUseCase {

    private final EmployeeRepository repository;

    public Employee findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Employee.class, "id", id));
    }

    public Page<Employee> findAll(EmployeeFilter filter) {
        var foundEmployees = repository.findAll(filter, filter.buildPageRequest());
        if (foundEmployees == null || foundEmployees.isEmpty())
            throw new ResourceNotFoundException(Employee.class);
        return foundEmployees;
    }
}
