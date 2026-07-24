package br.com.fiap.garage.domain.entity.assertions;

import br.com.fiap.garage.domain.entity.EstimatedMaterial;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.enums.MaterialType.SHOP_SUPPLY;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class EstimatedMaterialAssertions {

    private final EstimatedMaterial actual;

    public static EstimatedMaterialAssertions assertThat_EstimatedMaterial(EstimatedMaterial actual) {
        assertThat(actual).isNotNull();
        return new EstimatedMaterialAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.domain.entity.factory.MaterialFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_Material() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getMaterialId())
                .hasToString("73ffaed5-ebc3-4c28-901d-b0240c30b639");
        assertThat(actual.getType())
                .isEqualTo(SHOP_SUPPLY);
        assertThat(actual.getName())
                .isEqualTo("Engine Oil");
        assertThat(actual.getDescription())
                .isEqualTo("Synthetic 5W-30 motor oil");
        assertThat(actual.getCost())
                .isEqualByComparingTo(new BigDecimal("150.00"));

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