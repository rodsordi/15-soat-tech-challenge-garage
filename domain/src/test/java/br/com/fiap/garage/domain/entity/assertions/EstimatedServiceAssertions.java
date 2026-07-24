package br.com.fiap.garage.domain.entity.assertions;

import br.com.fiap.garage.domain.entity.EstimatedService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.assertions.EstimatedMaterialAssertions.assertThat_EstimatedMaterial;
import static br.com.fiap.garage.domain.entity.assertions.ServiceAssertions.assertThat_Service;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class EstimatedServiceAssertions {

    private final EstimatedService actual;

    public static EstimatedServiceAssertions assertThat_EstimatedService(EstimatedService actual) {
        assertThat(actual).isNotNull();
        return new EstimatedServiceAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_WorkOrderDto_Request() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getServiceId())
                .hasToString("");
        assertThat(actual.getName())
                .isNull();
        assertThat(actual.getDescription())
                .isNull();
        assertThat(actual.getCost())
                .isNull();
        assertThat(actual.getFinishedAt())
                .isNull();

        // Composition
        assertThat(actual.getEstimatedMaterials())
                .isNullOrEmpty();

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
     * @see br.com.fiap.garage.domain.entity.factory.ServiceFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_Service() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getServiceId())
                .hasToString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
        assertThat(actual.getName())
                .isEqualTo("Complete Engine Overhaul");
        assertThat(actual.getDescription())
                .isEqualTo("Comprehensive engine inspection, repair, and parts replacement");
        assertThat(actual.getCost())
                .isEqualByComparingTo(new BigDecimal("3500.00"));
        assertThat(actual.getFinishedAt())
                .isNull();

        // Composition
        assertThat_EstimatedMaterial(actual.getEstimatedMaterials().stream().findFirst().orElseThrow())
                .wasConvertedFrom_Material();

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