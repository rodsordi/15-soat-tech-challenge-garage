package br.com.fiap.garage.application.v1.def;

import br.com.fiap.commons.def.AuditableDef;
import br.com.fiap.garage.application.v1.controller.ServiceController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * | dto            | Request     | Response             |
 * | Representation | Represented | RepresentedPersisted |
 * | Detailed       | Detailed    | DetailedPersisted    |
 */
public interface ServiceDef {

    interface Represented {

        @Schema(example = "Oil Change", description = "Service name. Owner: self")
        @NotBlank
        @Size(max = 255)
        String getName();

        @Schema(example = "Oil Changing for car maintenance", description = "Service description. Owner: self")
        @Size(max = 255)
        String getDescription();

        @Schema(example = "19.99", description = "Service cost. Owner: self")
        @NotNull
        BigDecimal getCost();
    }

    interface Detailed extends Represented {

    }

    interface RepresentedPersisted extends AuditableDef.RepresentedPersisted {

        @JsonProperty(index = 1)
        @Schema(example = "1723546e-37e4-4692-863c-9d00be8aae1b", description = "Service id. Owner: db")
        @NotNull
        UUID getId();

        @Schema(example = "10", description = "Service average time in minutes. Owner: self")
        Long getAverageTimeInMinutes();
    }

    interface DetailedPersisted extends AuditableDef.DetailedPersisted, RepresentedPersisted {

    }

    interface Request extends Detailed {

        @Schema(example = "[1723546e-37e4-4692-863c-9d00be8aae1b]", description = "Materials ids. Owner: db")
        Set<UUID> getMaterialsIds();
    }

    interface Response extends Detailed, DetailedPersisted {

        <T extends MaterialDef.Representation> Set<T> getMaterials();
    }

    interface Representation extends Represented, RepresentedPersisted {

        @JsonIgnore
        default Class<?> getControllerClass() {
            return ServiceController.class;
        }
    }

    interface PutRequest extends Request {

    }
}
