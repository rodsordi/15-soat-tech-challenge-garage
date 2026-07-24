package br.com.fiap.garage.domain.entity.work_order.state;

import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import br.com.fiap.garage.domain.entity.work_order.WorkOrderState;

import static br.com.fiap.garage.domain.enums.WorkOrderStatus.EXECUTING;

public class WaitingForApprovalState extends WorkOrderState {

    public WaitingForApprovalState(WorkOrder workOrder) {
        super(workOrder);
    }

    @Override
    public WorkOrderStatus execute() {
        return EXECUTING;
    }
}
