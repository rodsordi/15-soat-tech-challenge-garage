package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.InventoryMaterial;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.MaterialFactory.create_Material;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class InventoryMaterialFactory {

    private final InventoryMaterial.InventoryMaterialBuilder<?, ?> builder;

    public static InventoryMaterialFactory create_InventoryMaterial() {
        return new InventoryMaterialFactory(InventoryMaterial.builder());
    }

    public InventoryMaterial withAllFields() {
        var result = builder
                // Self
                .id(fromString("9d1b9b7c-bd7c-4f5f-a747-0b1f63aac409"))
                .quantityInStock(100)
                .reservedQuantity(15)
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

    public InventoryMaterial withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .material(create_Material().withAllFieldsExceptDB())
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public InventoryMaterial valid() {
        return builder
                .quantityInStock(50)
                .reservedQuantity(5)
                .material(create_Material().valid())
                .build();
    }

    public InventoryMaterial initiatedEmpty() {
        return builder
                .material(create_Material().initiatedEmpty())
                .build();
    }
}