package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.InventoryMaterial;
import br.com.fiap.garage.domain.repository.InventoryMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryMaterialUpdateUseCase {

    private final InventoryMaterialRepository repository;

    public InventoryMaterial update(UUID id, InventoryMaterial inventoryMaterial) {
        var foundInventoryMaterial = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(InventoryMaterial.class, "id", id));
        foundInventoryMaterial.update(inventoryMaterial);
        return repository.save(foundInventoryMaterial);
    }

    public InventoryMaterial addQuantityToStock(UUID id, int quantityToBeAddedToStock) {
        var foundInventoryMaterial = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(InventoryMaterial.class, "id", id));
        foundInventoryMaterial.addQuantityToStock(quantityToBeAddedToStock);
        return repository.save(foundInventoryMaterial);
    }

    public InventoryMaterial addReservedQuantity(UUID id, int quantityToBeReserved) {
        var foundInventoryMaterial = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(InventoryMaterial.class, "id", id));
        foundInventoryMaterial.reserveQuantity(quantityToBeReserved);
        return repository.save(foundInventoryMaterial);
    }

    public InventoryMaterial concludeReservedQuantity(UUID id, int reservedQuantityToBeConcluded) {
        var foundInventoryMaterial = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(InventoryMaterial.class, "id", id));
        foundInventoryMaterial.concludeReservedQuantity(reservedQuantityToBeConcluded);
        return repository.save(foundInventoryMaterial);
    }
}
