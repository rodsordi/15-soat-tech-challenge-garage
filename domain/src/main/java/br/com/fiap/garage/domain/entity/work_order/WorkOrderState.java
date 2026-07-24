package br.com.fiap.garage.domain.entity.work_order;

import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import br.com.fiap.commons.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.garage.domain.enums.WorkOrderStatus.*;
import static java.lang.String.format;
import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor(access = PROTECTED)
public abstract class WorkOrderState {

    private static final String MSG = "WorkOrder in %s status, cannot be updated to %s status.";

    protected final WorkOrder workOrder;

    public WorkOrderStatus diagnose() {
        throw new BusinessException(format(MSG, workOrder.getStatus(), DIAGNOSING));
    }

    public WorkOrderStatus waitForApproval() {
        throw new BusinessException(format(MSG, workOrder.getStatus(), WAITING_FOR_APPROVAL));
    }

    public WorkOrderStatus execute() {
        throw new BusinessException(format(MSG, workOrder.getStatus(), EXECUTING));
    }

    public WorkOrderStatus finish() {
        throw new BusinessException(format(MSG, workOrder.getStatus(), FINISHED));
    }

    public WorkOrderStatus release() {
        throw new BusinessException(format(MSG, workOrder.getStatus(), RELEASED));
    }
}
