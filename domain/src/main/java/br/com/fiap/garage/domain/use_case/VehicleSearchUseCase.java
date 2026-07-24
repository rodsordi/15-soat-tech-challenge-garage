package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Vehicle;
import br.com.fiap.garage.domain.filter.VehicleFilter;
import br.com.fiap.garage.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleSearchUseCase {

    private final VehicleRepository repository;

    public Vehicle findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Vehicle.class, "id", id));
    }

    public Page<Vehicle> findAll(VehicleFilter filter) {
        var foundVehicles = repository.findAll(filter, filter.buildPageRequest());
        if (foundVehicles == null || foundVehicles.isEmpty())
            throw new ResourceNotFoundException(Vehicle.class);
        return foundVehicles;
    }
}
