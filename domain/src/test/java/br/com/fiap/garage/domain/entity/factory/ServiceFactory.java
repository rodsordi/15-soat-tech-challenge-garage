package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.MaterialFactory.create_Material;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ServiceFactory {

    private final Service.ServiceBuilder<?, ?> builder;

    public static ServiceFactory create_Service() {
        return new ServiceFactory(Service.builder());
    }

    public Service withAllFields() {
        var result = builder
                // Self
                .id(fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"))
                .name("Complete Engine Overhaul")
                .description("Comprehensive engine inspection, repair, and parts replacement")
                .cost(new BigDecimal("3500.00"))
                .averageTimeInMinutes(0L)
                // Composition
                .material(create_Material().withAllFields())
                // Inheritance (AuditableEntity)
                .createdAt(newDateTime("13/12/2026 23:59:59"))
                .updatedAt(newDateTime("14/12/2026 23:59:59"))
                .build();
        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public Service withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .clearMaterials()
                .material(create_Material().withAllFieldsExceptDB())
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public Service valid() {
        return builder
                .name("Complete Engine Overhaul")
                .description("Comprehensive engine inspection, repair, and parts replacement")
                .cost(new BigDecimal("3500.00"))
                .material(create_Material().withAllFields())
                .build();
    }

    public Service initiatedEmpty() {
        return builder
                .clearMaterials()
                .build();
    }
}