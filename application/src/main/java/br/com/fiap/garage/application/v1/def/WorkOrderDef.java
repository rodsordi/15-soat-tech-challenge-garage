package br.com.fiap.garage.application.v1.def;

import br.com.fiap.commons.def.AuditableDef;
import br.com.fiap.garage.application.v1.controller.WorkOrderController;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * | dto            | Request     | Response             |
 * | Representation | Represented | RepresentedPersisted |
 * | Detailed       | Detailed    | DetailedPersisted    |
 */
public interface WorkOrderDef {

    interface Represented {
    }

    interface Detailed extends Represented {

    }

    interface RepresentedPersisted extends AuditableDef.RepresentedPersisted {

        @JsonProperty(index = 1)
        @Schema(example = "819d14e1-ad48-4e60-8192-b15ad37f66a5", description = "Work Order id. Owner: db")
        @NotNull
        UUID getId();

        @NotNull
        WorkOrderStatus getStatus();

        @Schema(example = "99.99", description = "Work Order total amount estimation. Owner: self")
        @NotNull
        BigDecimal getTotalAmount();
    }

    interface DetailedPersisted extends AuditableDef.DetailedPersisted, RepresentedPersisted {

    }

    interface Request extends Detailed {

        // Aggregation
        @Schema(example = "721c3c29-3a9d-4c6e-8750-4bf16db0040b", description = "Vehicle id. Owner: db")
        @NotNull
        UUID getVehicleId();

        // Aggregation
        @Schema(example = "53978ee4-c4dd-4ddc-bcc5-75b408d71138", description = "Employee id. Owner: db")
        @NotNull
        UUID getEmployeeId();

        // Aggregation
        @ArraySchema(arraySchema = @Schema(description = "Services ids",
                example = "[57975909-7dcc-4cc3-ae4b-5b8823dc8e54, 3441475a-0884-42d5-b872-f91fb9174ffd]"))
        @NotEmpty
        Set<UUID> getServicesIds();
    }

    interface Response extends Detailed, DetailedPersisted {

        // Aggregation
        <T extends VehicleDef.Representation> T getVehicle();

        // Aggregation
        <T extends EmployeeDef.Representation> T getEmployee();

        // Aggregation
        <T extends EstimatedServiceDef.Response> Set<T> getEstimatedServices();
    }

    interface Representation extends Represented, RepresentedPersisted {

        @JsonIgnore
        default Class<?> getControllerClass() {
            return WorkOrderController.class;
        }
    }


    interface PatchRequest {

        WorkOrderStatus getStatus();

        UUID getEmployeeId();

        UUID getFinishedServiceId();
    }
}
