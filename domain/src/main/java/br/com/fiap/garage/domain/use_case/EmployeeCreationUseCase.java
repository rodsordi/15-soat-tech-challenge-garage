package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.Employee;
import br.com.fiap.garage.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeCreationUseCase {

    private final EmployeeRepository repository;

    @Transactional
    public Employee create(Employee employee) {
        var existingOpt = repository.findByCpf(employee.getCpf());
        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();
            if (employee.getId() == null || existing.getId().equals(employee.getId())) {
                existing.update(employee);
                return repository.save(existing);
            }
            repository.updateIdentity(existing.getId(), employee.getId(), employee.getName(), employee.getEmail());
            return repository.findById(employee.getId())
                    .orElse(employee);
        }
        return repository.save(employee);
    }
}
