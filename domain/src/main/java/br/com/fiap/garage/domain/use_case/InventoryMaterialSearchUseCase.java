package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.InventoryMaterial;
import br.com.fiap.garage.domain.filter.InventoryMaterialFilter;
import br.com.fiap.garage.domain.repository.InventoryMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryMaterialSearchUseCase {

    private final InventoryMaterialRepository inventoryMaterialRepository;

    public InventoryMaterial findById(UUID id) {
        return inventoryMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(InventoryMaterial.class, "id", id));
    }

    public Page<InventoryMaterial> findAll(InventoryMaterialFilter filter) {
        var foundInventories = inventoryMaterialRepository.findAll(filter, filter.buildPageRequest());
        if (foundInventories == null || foundInventories.isEmpty())
            throw new ResourceNotFoundException(InventoryMaterial.class);
        return foundInventories;
    }
}
