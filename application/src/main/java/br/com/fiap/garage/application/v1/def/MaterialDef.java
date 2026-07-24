package br.com.fiap.garage.application.v1.def;

import br.com.fiap.commons.def.AuditableDef;
import br.com.fiap.garage.domain.enums.MaterialType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * | dto            | Request     | Response             |
 * | Representation | Represented | RepresentedPersisted |
 * | Detailed       | Detailed    | DetailedPersisted    |
 */
public interface MaterialDef {

    interface Represented {

        @NotNull
        MaterialType getType();

        @Schema(example = "Oil", description = "Material name. Owner: self")
        @NotBlank
        @Size(max = 255)
        String getName();

        @Schema(example = "59.99", description = "Material cost. Owner: self")
        @NotNull
        BigDecimal getCost();
    }

    interface Detailed extends Represented {

        @Schema(example = "Synthetic 0W-20", description = "Material description. Owner: self")
        @Size(max = 255)
        String getDescription();
    }

    interface RepresentedPersisted extends AuditableDef.RepresentedPersisted {

        @JsonProperty(index = 1)
        @Schema(example = "dec86af2-75f9-4568-92df-10969f1dedf2", description = "Material id. Owner: db")
        @NotNull
        UUID getId();
    }

    interface DetailedPersisted extends AuditableDef.DetailedPersisted, RepresentedPersisted {

    }

    interface Request extends Detailed {

    }

    interface Response extends Detailed, DetailedPersisted {

    }

    interface Representation extends Represented, RepresentedPersisted {

    }
}