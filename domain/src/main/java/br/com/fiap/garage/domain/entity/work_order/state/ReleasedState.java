package br.com.fiap.garage.domain.entity.work_order.state;

import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.entity.work_order.WorkOrderState;

public class ReleasedState extends WorkOrderState {

    public ReleasedState(WorkOrder workOrder) {
        super(workOrder);
    }
}
