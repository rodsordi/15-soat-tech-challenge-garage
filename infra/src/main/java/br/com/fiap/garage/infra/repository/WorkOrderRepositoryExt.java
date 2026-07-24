package br.com.fiap.garage.infra.repository;

import br.com.fiap.garage.domain.repository.WorkOrderRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface WorkOrderRepositoryExt extends WorkOrderRepository {
    
}
