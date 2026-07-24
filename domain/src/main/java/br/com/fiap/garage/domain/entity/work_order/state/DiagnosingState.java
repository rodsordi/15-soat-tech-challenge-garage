package br.com.fiap.garage.domain.entity.work_order.state;

import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import br.com.fiap.garage.domain.entity.work_order.WorkOrderState;

import static br.com.fiap.garage.domain.enums.WorkOrderStatus.WAITING_FOR_APPROVAL;

public class DiagnosingState extends WorkOrderState {

    public DiagnosingState(WorkOrder workOrder) {
        super(workOrder);
    }

    @Override
    public WorkOrderStatus waitForApproval() {
        return WAITING_FOR_APPROVAL;
    }
}
