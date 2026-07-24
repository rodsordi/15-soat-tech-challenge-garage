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
public class ServiceUpdateUseCase {

    private final ServiceRepository serviceRepository;

    private final MaterialRepository materialRepository;

    public Service update(UUID id, Set<UUID> materialsIds, Service service) {
        var foundService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Service.class, "id", id));
        var materials = loadMaterials(materialsIds);
        service.updateMaterialsReference(materials);
        foundService.update(service);
        return serviceRepository.save(foundService);
    }

    private Set<Material> loadMaterials(Set<UUID> materialsIds) {
        return materialsIds.stream()
                .map(materialId -> materialRepository.findById(materialId)
                        .orElseThrow(() -> new ResourceNotFoundException(Material.class, "id", materialId)))
                .collect(Collectors.toSet());
    }

    public void calculateAverageTime() {
        //TODO: Not ideal but it is like it is for a while
        var services = serviceRepository.findAll();
        for (var service : services) {
            var avgTimeInMinutes = serviceRepository.calculateAverageTimeOfServiceInMinutes(service.getId());
            service.updateAverageTime(avgTimeInMinutes);
            serviceRepository.save(service);
        }
    }
}
