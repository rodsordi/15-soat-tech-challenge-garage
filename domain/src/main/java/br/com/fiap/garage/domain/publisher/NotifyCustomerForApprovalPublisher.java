package br.com.fiap.garage.domain.publisher;

import br.com.fiap.garage.domain.entity.WorkOrder;

public interface NotifyCustomerForApprovalPublisher {

    void notify(WorkOrder workOrder);
}
