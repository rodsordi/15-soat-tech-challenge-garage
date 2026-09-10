package br.com.fiap.commons.config;

import org.hibernate.persister.collection.BasicCollectionPersister;
import org.hibernate.persister.collection.OneToManyPersister;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.entity.UnionSubclassEntityPersister;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import java.util.UUID;

public class NativeRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(UUID.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS);

        hints.reflection().registerType(UUID[].class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        hints.reflection().registerType(TypeReference.of("java.util.UUID[]"),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        hints.reflection().registerType(JoinedSubclassEntityPersister.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS);

        hints.reflection().registerType(SingleTableEntityPersister.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS);

        hints.reflection().registerType(UnionSubclassEntityPersister.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS);

        hints.reflection().registerType(BasicCollectionPersister.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        hints.reflection().registerType(OneToManyPersister.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        java.util.List.of(
                br.com.fiap.garage.domain.entity.Customer.class,
                br.com.fiap.garage.domain.entity.Employee.class,
                br.com.fiap.garage.domain.entity.Vehicle.class,
                br.com.fiap.garage.domain.entity.Email.class,
                br.com.fiap.garage.domain.entity.Notification.class,
                br.com.fiap.garage.domain.entity.Material.class,
                br.com.fiap.garage.domain.entity.InventoryMaterial.class,
                br.com.fiap.garage.domain.entity.Service.class,
                br.com.fiap.garage.domain.entity.EstimatedService.class,
                br.com.fiap.garage.domain.entity.EstimatedMaterial.class,
                br.com.fiap.garage.domain.entity.WorkOrder.class,
                br.com.fiap.commons.entity.AuditableEntity.class,
                br.com.fiap.commons.entity.ImmutableAuditableEntity.class,
                br.com.fiap.garage.domain.enums.MaterialType.class,
                br.com.fiap.garage.domain.enums.WorkOrderStatus.class
        ).forEach(type -> hints.reflection().registerType(type,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.DECLARED_FIELDS));
    }
}
