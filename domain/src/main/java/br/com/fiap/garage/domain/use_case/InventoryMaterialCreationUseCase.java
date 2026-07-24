package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.InventoryMaterial;
import br.com.fiap.garage.domain.repository.InventoryMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryMaterialCreationUseCase {

    private final InventoryMaterialRepository repository;

    public InventoryMaterial create(InventoryMaterial inventoryMaterial) {
        return repository.save(inventoryMaterial);
    }
}
