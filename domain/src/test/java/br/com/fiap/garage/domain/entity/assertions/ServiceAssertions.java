package br.com.fiap.garage.domain.entity.assertions;

import br.com.fiap.garage.domain.entity.Service;
import br.com.fiap.garage.domain.entity.factory.ServiceFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.assertions.MaterialAssertions.assertThat_Material;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class ServiceAssertions {

    private final Service actual;

    public static ServiceAssertions assertThat_Service(Service actual) {
        assertThat(actual).isNotNull();
        return new ServiceAssertions(spy(actual));
    }

    /**
     * @see ServiceFactory
     * .withAllFields()
     */
    public void isEqualTo_Service() {
        // Self
        assertThat(actual.getId())
                .hasToString("9d1b9b7c-bd7c-4f5f-a747-0b1f63aac409");
        assertThat(actual.getName())
                .isEqualTo("Standard Engine Maintenance");
        assertThat(actual.getDescription())
                .isEqualTo("Complete engine checkup and oil change");
        assertThat(actual.getCost())
                .isEqualTo(new BigDecimal("250.00"));
        assertThat(actual.getAverageTimeInMinutes())
                .isZero();

        // Composition (Many-to-Many)
        assertThat_Material(actual.getMaterials().stream().findFirst().orElseThrow())
                .wasUpdatedUsing_Material();

        // Inheritance (AuditableEntity)
        assertThat(actual.getCreatedAt())
                .isEqualTo(newDateTime("30/12/2024 23:59:59"));
        assertThat(actual.getUpdatedAt())
                .isEqualTo(newDateTime("31/12/2024 23:59:59"));

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }

    /**
     * @see br.com.fiap.garage.application.v1.dto.factory.ServiceDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_ServiceDto() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getName())
                .isEqualTo("Oil Change");
        assertThat(actual.getDescription())
                .isEqualTo("Complete engine oil and filter change");
        assertThat(actual.getCost())
                .isEqualTo(new BigDecimal("150.00"));
        assertThat(actual.getAverageTimeInMinutes())
                .isNull();

        // Composition (Many-to-Many)
        assertThat(actual.getMaterials())
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
     * @see br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_WorkOrderDto() {
        // Self
        assertThat(actual.getId())
                .hasToString("0913e18b-84bd-4619-ad0a-c77600960346");
        assertThat(actual.getName())
                .isNull();
        assertThat(actual.getDescription())
                .isNull();
        assertThat(actual.getCost())
                .isNull();
        assertThat(actual.getAverageTimeInMinutes())
                .isZero();

        // Composition (Many-to-Many)
        assertThat(actual.getMaterials())
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
}