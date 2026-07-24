package br.com.fiap.garage.application.v1.dto;

import br.com.fiap.garage.application.v1.def.EstimatedServiceDef;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class EstimatedServiceDto {

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "EstimatedService.Response")
    public static class Response implements EstimatedServiceDef.Response {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal cost;
        private LocalDateTime finishedAt;
        @Singular("estimatedMaterial")
        private Set<EstimatedMaterialDto.Response> estimatedMaterials;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
