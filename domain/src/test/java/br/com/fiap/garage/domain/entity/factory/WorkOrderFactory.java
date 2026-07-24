package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.WorkOrder;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
import static br.com.fiap.garage.domain.entity.factory.EmployeeFactory.create_Employee;
import static br.com.fiap.garage.domain.entity.factory.EstimatedServiceFactory.create_EstimatedService;
import static br.com.fiap.garage.domain.entity.factory.VehicleFactory.create_Vehicle;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.RECEIVED;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class WorkOrderFactory {

    private final WorkOrder.WorkOrderBuilder<?, ?> builder;

    public static WorkOrderFactory create_WorkOrder() {
        return new WorkOrderFactory(WorkOrder.builder());
    }

    public WorkOrder withAllFields() {
        var result = builder
                // Self
                .id(fromString("e48ad20c-69dd-4382-b567-0e02b2c3d480"))
                .status(RECEIVED)
                .totalAmount(new BigDecimal("999.99"))
                // Composition
                .vehicle(create_Vehicle().withAllFields())
                .employee(create_Employee().withAllFields())
                .estimatedService(create_EstimatedService().withAllFields())
                // Inheritance (AuditableEntity)
                .createdAt(newDateTime("13/12/2026 23:59:59"))
                .updatedAt(newDateTime("14/12/2026 23:59:59"))
                .build();

        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public WorkOrder withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .vehicle(create_Vehicle().withAllFieldsExceptDB())
                .employee(create_Employee().withAllFieldsExceptDB())
                .clearEstimatedServices()
                .estimatedService(create_EstimatedService().withAllFieldsExceptDB())
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public WorkOrder valid() {
        return builder
                .status(RECEIVED)
                .estimatedService(create_EstimatedService().valid())
                .build();
    }

    public WorkOrder initiatedEmpty() {
        return builder
                .estimatedService(create_EstimatedService().initiatedEmpty())
                .build();
    }
}