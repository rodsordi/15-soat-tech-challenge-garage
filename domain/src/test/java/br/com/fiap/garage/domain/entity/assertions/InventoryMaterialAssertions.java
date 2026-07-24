package br.com.fiap.garage.domain.entity.assertions;

import br.com.fiap.garage.domain.entity.InventoryMaterial;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.assertions.MaterialAssertions.assertThat_Material;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class InventoryMaterialAssertions {

    private final InventoryMaterial actual;

    public static InventoryMaterialAssertions assertThat_InventoryMaterial(InventoryMaterial actual) {
        assertThat(actual).isNotNull();
        return new InventoryMaterialAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory
     * .withAllFields()
     */
    public void wasUpdatedUsing_InventoryMaterial() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getQuantityInStock())
                .isEqualTo(100);
        assertThat(actual.getReservedQuantity())
                .isZero();
        assertThat_Material(actual.getMaterial())
                .wasUpdatedUsing_Material();

        // Inheritance (AuditableEntity)
        assertThat(actual.getCreatedAt())
                .isNull();
        assertThat(actual.getUpdatedAt())
                .isNull();

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }

    /**
     * @see br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_InventoryMaterialDto_Request() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getQuantityInStock())
                .isEqualTo(100);
        assertThat(actual.getReservedQuantity())
                .isZero();
        assertThat_Material(actual.getMaterial())
                .wasConvertedFrom_MaterialDto_Request();

        // Inheritance (AuditableEntity)
        assertThat(actual.getCreatedAt())
                .isNull();
        assertThat(actual.getUpdatedAt())
                .isNull();

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }
}