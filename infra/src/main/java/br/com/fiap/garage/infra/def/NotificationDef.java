package br.com.fiap.garage.infra.def;

import br.com.fiap.commons.def.AuditableDef;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * | dto            | Request     | Response             |
 * | Representation | Represented | RepresentedPersisted |
 * | Detailed       | Detailed    | DetailedPersisted    |
 */
public interface NotificationDef {

    interface Represented {

        @JsonProperty(index = 1)
        @Schema(example = "3a49a18d-5482-4ee6-8d19-c786a6489008", description = "External Client id. Owner: client")
        @NotNull
        UUID getExternalId();
    }

    interface Detailed extends Represented {

    }

    interface RepresentedPersisted extends AuditableDef.RepresentedPersisted {

        @Schema(example = "b9a16f67-e44e-4d55-90fa-0cc92b80049e", description = "Notification id. Owner: db")
        @NotNull
        UUID getId();
    }

    interface DetailedPersisted extends AuditableDef.DetailedPersisted, RepresentedPersisted {

    }

    interface Request extends Detailed {

        // Value Object
        <T extends EmailDef.Request> T getEmail();
    }

    interface Response extends Detailed, DetailedPersisted {

        // Value Object
        <T extends EmailDef.Response> T getEmail();
    }

    interface Representation extends Represented, RepresentedPersisted {

        // Value Object
        <T extends EmailDef.Representation> T getEmail();
    }
}
