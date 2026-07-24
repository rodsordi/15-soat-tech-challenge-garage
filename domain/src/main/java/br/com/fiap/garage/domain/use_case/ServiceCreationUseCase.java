package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Material;
import br.com.fiap.garage.domain.entity.Service;
import br.com.fiap.garage.domain.repository.MaterialRepository;
import br.com.fiap.garage.domain.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCreationUseCase {

    private final ServiceRepository serviceRepository;

    private final MaterialRepository materialRepository;

    public Service create(Service service, Set<UUID> materialsIds) {
        var materials = loadMaterials(materialsIds);
        service.updateMaterialsReference(materials);
        return serviceRepository.save(service);
    }

    private Set<Material> loadMaterials(Set<UUID> materialsIds) {
        return materialsIds.stream()
                .map(materialId -> materialRepository.findById(materialId)
                        .orElseThrow(() -> new ResourceNotFoundException(Material.class, "id", materialId)))
                .collect(Collectors.toSet());
    }
}
