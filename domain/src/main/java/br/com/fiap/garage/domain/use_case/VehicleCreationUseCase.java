package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Customer;
import br.com.fiap.garage.domain.entity.Vehicle;
import br.com.fiap.garage.domain.repository.CustomerRepository;
import br.com.fiap.garage.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleCreationUseCase {

    private final CustomerRepository customerRepository;

    private final VehicleRepository vehicleRepository;

    public Vehicle create(UUID customerId, Vehicle vehicle) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(Customer.class, "id", customerId));
        vehicle.setCustomer(customer);
        return vehicleRepository.save(vehicle);
    }
}
