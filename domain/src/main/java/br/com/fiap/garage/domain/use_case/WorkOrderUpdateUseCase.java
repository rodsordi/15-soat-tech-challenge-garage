package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.InternalErrorException;
import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Employee;
import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import br.com.fiap.garage.domain.publisher.NotifyCustomerForApprovalPublisher;
import br.com.fiap.garage.domain.repository.EmployeeRepository;
import br.com.fiap.garage.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static br.com.fiap.garage.domain.enums.WorkOrderStatus.WAITING_FOR_APPROVAL;

@Service
@RequiredArgsConstructor
public class WorkOrderUpdateUseCase {

    private final WorkOrderRepository workOrderRepository;

    private final EmployeeRepository employeeRepository;

    private final NotifyCustomerForApprovalPublisher notifyCustomerForApprovalPublisher;

    public WorkOrder updateStatus(UUID id, WorkOrderStatus status) {
        var foundWorkOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WorkOrder.class, "id", id));

        switch (status) {
            case DIAGNOSING -> foundWorkOrder.diagnose();
            case WAITING_FOR_APPROVAL -> foundWorkOrder.waitForApproval();
            case EXECUTING -> foundWorkOrder.execute();
            case FINISHED -> foundWorkOrder.finish();
            case RELEASED -> foundWorkOrder.release();
            default -> throw new InternalErrorException("Status not found");
        }

        if (WAITING_FOR_APPROVAL == status)
            notifyCustomerForApprovalPublisher.notify(foundWorkOrder);

        return workOrderRepository.save(foundWorkOrder);
    }

    public WorkOrder updateEmployee(UUID id, UUID employeeId) {
        var foundWorkOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WorkOrder.class, "id", id));

        var foundEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(Employee.class, "id", employeeId));

        foundWorkOrder.update(foundEmployee);

        return workOrderRepository.save(foundWorkOrder);
    }

    public WorkOrder finishService(UUID id, UUID serviceToBeFinished) {
        var foundWorkOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WorkOrder.class, "id", id));

        for (var estimatedService : foundWorkOrder.getEstimatedServices())
            if (estimatedService.getServiceId().toString().equals(serviceToBeFinished.toString()))
                estimatedService.finish();

        return workOrderRepository.save(foundWorkOrder);
    }
}
