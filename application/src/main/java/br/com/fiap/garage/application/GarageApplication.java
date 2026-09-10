package br.com.fiap.garage.application;

import br.com.fiap.commons.config.NativeRuntimeHints;
import br.com.fiap.commons.entity.AuditableEntity;
import br.com.fiap.commons.entity.ImmutableAuditableEntity;
import br.com.fiap.garage.domain.entity.*;
import br.com.fiap.garage.domain.enums.MaterialType;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication(scanBasePackages = "br.com.fiap")
@RegisterReflectionForBinding({
        Customer.class,
        Employee.class,
        Vehicle.class,
        Email.class,
        Notification.class,
        Material.class,
        InventoryMaterial.class,
        Service.class,
        EstimatedService.class,
        EstimatedMaterial.class,
        WorkOrder.class,
        AuditableEntity.class,
        ImmutableAuditableEntity.class,
        MaterialType.class,
        WorkOrderStatus.class
})
@ImportRuntimeHints(NativeRuntimeHints.class)
public class GarageApplication {

    static void main(String[] args) {
        run(GarageApplication.class, args);
    }
}
