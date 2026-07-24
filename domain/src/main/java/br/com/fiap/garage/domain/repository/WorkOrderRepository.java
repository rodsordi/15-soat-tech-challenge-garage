package br.com.fiap.garage.domain.repository;

import br.com.fiap.garage.domain.entity.WorkOrder;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

@Order(4)
public interface WorkOrderRepository extends CrudRepository<WorkOrder, UUID> {

    Page<WorkOrder> findAll(Specification<WorkOrder> filter, Pageable pageable);
}
