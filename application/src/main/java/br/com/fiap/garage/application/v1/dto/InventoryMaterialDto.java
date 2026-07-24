package br.com.fiap.garage.application.v1.dto;

import br.com.fiap.garage.application.v1.def.InventoryMaterialDef;
import br.com.fiap.garage.application.v1.mapper.InventoryMaterialDtoMapper;
import br.com.fiap.garage.domain.entity.InventoryMaterial;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@NoArgsConstructor(access = PRIVATE)
public final class InventoryMaterialDto {

    private static final InventoryMaterialDtoMapper MAPPER = getMapper(InventoryMaterialDtoMapper.class);

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "InventoryMaterial.Request")
    public static class Request implements InventoryMaterialDef.Request {
        private Integer quantityInStock;
        private MaterialDto.Request material;

        public InventoryMaterial buildInventoryMaterial() {
            return MAPPER.convert(this);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "InventoryMaterial.Response")
    public static class Response implements InventoryMaterialDef.Response {
        private UUID id;
        private Integer quantityInStock;
        private Integer reservedQuantity;
        private MaterialDto.Response material;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static InventoryMaterialDto.Response buildInventoryMaterialDtoResponse(InventoryMaterial inventory) {
            return MAPPER.convert(inventory);
        }
    }
    
    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @EqualsAndHashCode(callSuper = true)
    @Schema(name = "InventoryMaterial.Representation")
    public static class Representation extends RepresentationModel<Representation> implements InventoryMaterialDef.Representation {
        private UUID id;
        private Integer quantityInStock;
        private Integer reservedQuantity;
        private MaterialDto.Representation material;
        private LocalDateTime createdAt;

        public UUID getId() {
            add(linkTo(getControllerClass())
                    .slash(id)
                    .withSelfRel());
            return id;
        }

        public static InventoryMaterialDto.Representation buildInventoryDtoRepresentation(InventoryMaterial inventoryMaterial) {
            return MAPPER.convertToRepresentation(inventoryMaterial);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "InventoryMaterial.PutRequest")
    public static class PutRequest implements InventoryMaterialDef.PutRequest {
        private Integer quantityInStock;
        private MaterialDto.Request material;

        public InventoryMaterial buildInventoryMaterial() {
            return MAPPER.convert(this);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "InventoryMaterial.PatchRequest")
    public static class PatchRequest implements InventoryMaterialDef.PatchRequest {
        private Integer quantityToBeAddedToStock;
        private Integer quantityToBeReserved;
        private Integer reservedQuantityToBeConcluded;
    }
}
