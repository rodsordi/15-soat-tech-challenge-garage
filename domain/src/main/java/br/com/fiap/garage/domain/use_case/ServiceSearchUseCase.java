package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Service;
import br.com.fiap.garage.domain.filter.ServiceFilter;
import br.com.fiap.garage.domain.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceSearchUseCase {

    private final ServiceRepository repository;

    public Service findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Service.class, "id", id));
    }

    public Page<Service> findAll(ServiceFilter filter) {
        var foundServices = repository.findAll(filter, filter.buildPageRequest());
        if (foundServices == null || foundServices.isEmpty())
            throw new ResourceNotFoundException(Service.class);
        return foundServices;
    }
}
