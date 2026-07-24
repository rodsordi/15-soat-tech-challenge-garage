package br.com.fiap.garage.domain.entity.assertions;

import br.com.fiap.garage.domain.entity.WorkOrder;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.assertions.EmployeeAssertions.assertThat_Employee;
import static br.com.fiap.garage.domain.entity.assertions.EstimatedServiceAssertions.assertThat_EstimatedService;
import static br.com.fiap.garage.domain.entity.assertions.VehicleAssertions.assertThat_Vehicle;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.RECEIVED;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class WorkOrderAssertions {

    private final WorkOrder actual;

    public static WorkOrderAssertions assertThat_WorkOrder(WorkOrder actual) {
        assertThat(actual).isNotNull();
        return new WorkOrderAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_WorkOrderDto_Request() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getStatus())
                .isEqualTo(RECEIVED);
        assertThat(actual.getTotalAmount())
                .isNull();

        // Composition
        assertThat_Vehicle(actual.getVehicle())
                .wasConvertedFrom_WorkOrderDto_Request();
        assertThat_Employee(actual.getEmployee())
                .wasConvertedFrom_WorkOrderDto_Request();
        assertThat(actual.getEstimatedServices())
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