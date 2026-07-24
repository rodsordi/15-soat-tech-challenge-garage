package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.filter.WorkOrderFilter;
import br.com.fiap.garage.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkOrderSearchUseCase {

    private final WorkOrderRepository repository;

    public WorkOrder findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(WorkOrder.class, "id", id));
    }

    public Page<WorkOrder> findAll(WorkOrderFilter filter) {
        var foundWorkOrders = repository.findAll(filter, filter.buildPageRequest());
        if (foundWorkOrders == null || foundWorkOrders.isEmpty())
            throw new ResourceNotFoundException(WorkOrder.class);
        return foundWorkOrders;
    }
}
