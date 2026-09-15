package br.com.fiap.garage.application.adapter.repository;

import br.com.fiap.garage.domain.repository.CustomerRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Primary
@Repository
public interface CustomerRepositoryExt extends CustomerRepository {

    @Modifying
    @Transactional
    @Query(value = "UPDATE garage.vehicle SET customer_id = :newId WHERE customer_id = :oldId", nativeQuery = true)
    void updateVehicleCustomerId(@Param("oldId") UUID oldId, @Param("newId") UUID newId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE garage.customer SET id = :newId, name = :name, email = :email, updated_at = NOW() WHERE id = :oldId", nativeQuery = true)
    void updateCustomerRecord(@Param("oldId") UUID oldId, @Param("newId") UUID newId, @Param("name") String name, @Param("email") String email);

    @Override
    @Transactional
    default void updateIdentity(UUID oldId, UUID newId, String name, String email) {
        updateVehicleCustomerId(oldId, newId);
        updateCustomerRecord(oldId, newId, name, email);
    }
}
