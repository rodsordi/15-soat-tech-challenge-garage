package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.EstimatedMaterial;
import br.com.fiap.garage.domain.entity.EstimatedService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.EstimatedMaterialFactory.create_EstimatedMaterial;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class EstimatedServiceFactory {

    private final EstimatedService.EstimatedServiceBuilder<?, ?> builder;

    public static EstimatedServiceFactory create_EstimatedService() {
        return new EstimatedServiceFactory(EstimatedService.builder());
    }

    public EstimatedService withAllFields() {
        var result = builder
                // Self
                .id(2L)
                .serviceId(UUID.fromString("b69f475d-40b4-41bc-b0ab-22500db01821"))
                .name("Engine Overhaul")
                .description("Complete disassembly and rebuilding of the engine block.")
                .cost(new BigDecimal("4500.00"))
                .finishedAt(newDateTime("31/12/2025 18:00:00"))
                // Composition
                .estimatedMaterial(create_EstimatedMaterial().withAllFields())
                // Inheritance (AuditableEntity)
                .createdAt(newDateTime("13/12/2026 23:59:59"))
                .updatedAt(newDateTime("14/12/2026 23:59:59"))
                .build();

        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public EstimatedService withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .clearEstimatedMaterials()
                .estimatedMaterial(create_EstimatedMaterial().withAllFieldsExceptDB())
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public EstimatedService valid() {
        return builder
                .name("Engine Overhaul")
                .cost(new BigDecimal("4500.00"))
                .estimatedMaterial(create_EstimatedMaterial().valid())
                .build();
    }

    public EstimatedService initiatedEmpty() {
        return builder
                .estimatedMaterial(EstimatedMaterial.builder().build())
                .build();
    }
}