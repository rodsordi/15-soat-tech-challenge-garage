package br.com.fiap.garage.application.v1.def;

import br.com.fiap.commons.def.AuditableDef;
import br.com.fiap.commons.validation.CpfOrCnpj;
import br.com.fiap.garage.application.v1.controller.CustomerController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

/**
 * | dto            | Request     | Response             |
 * | Representation | Represented | RepresentedPersisted |
 * | Detailed       | Detailed    | DetailedPersisted    |
 */
public interface CustomerDef {

    interface Represented {

        @Schema(example = "john.doe", description = "Customer username.")
        @NotBlank
        @Size(max = 255)
        String getUsername();

        @Schema(example = "John Doe", description = "Customer password.")
        @NotBlank
        @Size(max = 255)
        String getName();

        @Schema(example = "john.doe@email.com", description = "Customer e-mail.")
        @NotBlank
        @Size(max = 255)
        String getEmail();

        @Schema(example = "77.790.794/0001-69", description = "Customer document (CPF/CNPJ).")
        @NotBlank
        @Size(min = 11, max = 18)
        @CpfOrCnpj
        String getDocument();
    }

    interface Detailed extends Represented {

    }

    interface RepresentedPersisted extends AuditableDef.RepresentedPersisted {

        @JsonProperty(index = 1)
        @Schema(example = "36c9df52-01eb-4ffd-a0c1-1494440aedef", description = "Customer id.")
        @NotNull
        UUID getId();
    }

    interface DetailedPersisted extends AuditableDef.DetailedPersisted, RepresentedPersisted {

    }

    interface Request extends Detailed {

        @Schema(example = "ComplexPassword@2026", description = "Customer id.")
        @NotBlank
        @Size(max = 60)
        String getPassword();

        <T extends VehicleDef.Request> Set<T> getVehicles();
    }

    interface Response extends Detailed, DetailedPersisted {

        <T extends VehicleDef.Representation> Set<T> getVehicles();
    }

    interface Representation extends Represented, RepresentedPersisted {

        @JsonIgnore
        default Class<?> getControllerClass() {
            return CustomerController.class;
        }
    }

    interface PatchRequest {

        @Schema(example = "John Doe", description = "Customer password.")
        @Size(max = 255)
        String getName();

        @Schema(example = "john.doe@email.com", description = "Customer e-mail.")
        @Size(max = 255)
        String getEmail();
    }
}
