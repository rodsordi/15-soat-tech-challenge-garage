package br.com.fiap.garage.domain.entity.assertions;

import br.com.fiap.garage.domain.entity.Vehicle;
import lombok.RequiredArgsConstructor;

import java.time.Year;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class VehicleAssertions {

    private final Vehicle actual;

    public static VehicleAssertions assertThat_Vehicle(Vehicle actual) {
        assertThat(actual).isNotNull();
        return new VehicleAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.application.v1.dto.factory.VehicleDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_VehicleDto_Request() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getMake())
                .isEqualTo("Toyota");
        assertThat(actual.getModel())
                .isEqualTo("Corolla");
        assertThat(actual.getLicensePlate())
                .isEqualTo("ABC1234");
        assertThat(actual.getManufactureYear())
                .isEqualTo(Year.parse("2026"));

        // Composition
        assertThat(actual.getCustomer())
                .isNull();

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
    public void wasConvertedFrom_WorkOrderDto_Request() {
        // Self
        assertThat(actual.getId())
                .hasToString("5b3b7f42-0a9f-4093-82af-a7db99131e7c");
        assertThat(actual.getMake())
                .isNull();
        assertThat(actual.getModel())
                .isNull();
        assertThat(actual.getLicensePlate())
                .isNull();
        assertThat(actual.getManufactureYear())
                .isNull();

        // Composition
        assertThat(actual.getCustomer())
                .isNull();

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