package br.com.fiap.garage.application.v1.def;

import br.com.fiap.commons.def.AuditableDef;
import br.com.fiap.garage.application.v1.controller.EmployeeController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;

/**
 * | dto            | Request     | Response             |
 * | Representation | Represented | RepresentedPersisted |
 * | Detailed       | Detailed    | DetailedPersisted    |
 */
public interface EmployeeDef {

    interface Represented {

        @Schema(example = "jack.doe", description = "Employee username.")
        @Size(max = 255)
        String getUsername();

        @Schema(example = "Jack Doe", description = "Employee password.")
        @NotBlank
        @Size(max = 255)
        String getName();

        @Schema(example = "jack.doe@email.com", description = "Employee e-mail.")
        @NotBlank
        @Size(max = 255)
        String getEmail();

        @Schema(example = "214.454.220-18", description = "Employee cpf.")
        @NotBlank
        @Size(min = 11, max = 14)
        @CPF
        String getCpf();
    }

    interface Detailed extends Represented {

    }

    interface RepresentedPersisted extends AuditableDef.RepresentedPersisted {

        @JsonProperty(index = 1)
        @Schema(example = "a0949107-8b0e-4e28-b541-ecc34bb35b1d", description = "Employee id.")
        @NotNull
        UUID getId();
    }

    interface DetailedPersisted extends AuditableDef.DetailedPersisted, RepresentedPersisted {

    }

    interface Request extends Detailed {

        @Schema(example = "ComplexPassword@2026", description = "Employee id.")
        @NotBlank
        @Size(max = 60)
        String getPassword();
    }

    interface Response extends Detailed, DetailedPersisted {

    }

    interface Representation extends Represented, RepresentedPersisted {

        @JsonIgnore
        default Class<?> getControllerClass() {
            return EmployeeController.class;
        }
    }

    interface PatchRequest {

        @Schema(example = "Jack Doe", description = "Employee password.")
        @Size(max = 255)
        String getName();

        @Schema(example = "jack.doe@email.com", description = "Employee e-mail.")
        @Size(max = 255)
        String getEmail();
    }
}
