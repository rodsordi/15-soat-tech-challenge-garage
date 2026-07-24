package br.com.fiap.garage.application.v1.def;

import br.com.fiap.commons.def.AuditableDef;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

/**
 * | dto            | Request     | Response             |
 * | Representation | Represented | RepresentedPersisted |
 * | Detailed       | Detailed    | DetailedPersisted    |
 */
public interface EstimatedServiceDef {

    interface RepresentedPersisted extends AuditableDef.RepresentedPersisted {

        @JsonProperty(index = 1)
        @NotNull
        UUID getId();

        @Schema(example = "Oil Change", description = "Estimated Service name. Owner: self")
        @NotBlank
        @Size(max = 255)
        String getName();

        @Schema(example = "19.99", description = "Estimated Service cost. Owner: self")
        @NotNull
        BigDecimal getCost();

        @Schema(example = "2026-12-13T23:59:59", format = "date-time", description = "Estimated Service finished at. Owner: self")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = STRING)
        LocalDateTime getFinishedAt();
    }

    interface DetailedPersisted extends AuditableDef.DetailedPersisted, RepresentedPersisted {

        @Schema(example = "Estimated Service description. Owner: self", description = "Service description. Owner: self")
        @Size(max = 255)
        String getDescription();
    }

    interface Response extends DetailedPersisted {

        <T extends EstimatedMaterialDef.Response> Set<T> getEstimatedMaterials();
    }
}
