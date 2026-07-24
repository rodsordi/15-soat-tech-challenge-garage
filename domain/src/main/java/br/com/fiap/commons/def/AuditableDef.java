package br.com.fiap.commons.def;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public interface AuditableDef {

    interface RepresentedPersisted {

        @Schema(example = "2025-12-31T23:59:59", format = "date-time", description = "Register created at.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = STRING)
        @NotNull
        LocalDateTime getCreatedAt();
    }

    interface DetailedPersisted extends RepresentedPersisted {

        @Schema(example = "2025-12-31T23:59:59", format = "date-time", description = "Register updated at.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = STRING)
        LocalDateTime getUpdatedAt();
    }
}
