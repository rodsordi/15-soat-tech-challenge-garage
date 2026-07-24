package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.Material;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.enums.MaterialType.SHOP_SUPPLY;
import static br.com.fiap.garage.domain.enums.MaterialType.SPARE_PART;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class MaterialFactory {

    private final Material.MaterialBuilder<?, ?> builder;

    public static MaterialFactory create_Material() {
        return new MaterialFactory(Material.builder());
    }

    public Material withAllFields() {
        var result = builder
                // Self
                .id(UUID.fromString("73ffaed5-ebc3-4c28-901d-b0240c30b639"))
                .type(SHOP_SUPPLY)
                .name("Engine Oil")
                .description("Synthetic 5W-30 motor oil")
                .cost(new BigDecimal("150.00"))
                // Inheritance (AuditableEntity)
                .createdAt(newDateTime("13/12/2026 23:59:59"))
                .updatedAt(newDateTime("14/12/2026 23:59:59"))
                .build();

        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public Material withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public Material valid() {
        return builder
                .name("Brake Pad")
                .type(SPARE_PART)
                .cost(new BigDecimal("85.50"))
                .build();
    }

    public Material initiatedEmpty() {
        return builder.build();
    }
}