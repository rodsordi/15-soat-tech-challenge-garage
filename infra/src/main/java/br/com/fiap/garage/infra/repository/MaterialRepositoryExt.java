package br.com.fiap.garage.infra.repository;

import br.com.fiap.garage.domain.repository.MaterialRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface MaterialRepositoryExt extends MaterialRepository {
    
}
