package br.com.fiap.garage.application.adapter.repository;

import br.com.fiap.garage.domain.repository.EmployeeRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Primary
@Repository
public interface EmployeeRepositoryExt extends EmployeeRepository {

    @Modifying
    @Transactional
    @Query(value = "UPDATE garage.work_order SET employee_id = :newId WHERE employee_id = :oldId", nativeQuery = true)
    void updateWorkOrderEmployeeId(@Param("oldId") UUID oldId, @Param("newId") UUID newId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE garage.employee SET id = :newId, name = :name, email = :email, updated_at = NOW() WHERE id = :oldId", nativeQuery = true)
    void updateEmployeeRecord(@Param("oldId") UUID oldId, @Param("newId") UUID newId, @Param("name") String name, @Param("email") String email);

    @Override
    @Transactional
    default void updateIdentity(UUID oldId, UUID newId, String name, String email) {
        updateWorkOrderEmployeeId(oldId, newId);
        updateEmployeeRecord(oldId, newId, name, email);
    }
}
