package br.com.fiap.garage.application.v1.dto;

import br.com.fiap.garage.application.v1.def.ServiceDef;
import br.com.fiap.garage.application.v1.mapper.ServiceDtoMapper;
import br.com.fiap.garage.domain.entity.Service;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@NoArgsConstructor(access = PRIVATE)
public final class ServiceDto {

    public static final ServiceDtoMapper MAPPER = getMapper(ServiceDtoMapper.class);

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Service.Request")
    public static class Request implements ServiceDef.Request {
        private String name;
        private String description;
        private BigDecimal cost;
        @Singular(value = "materialId", ignoreNullCollections = true)
        private Set<UUID> materialsIds;

        public Service buildService() {
            return MAPPER.convert(this);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Service.Response")
    public static class Response implements ServiceDef.Response {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal cost;
        private Long averageTimeInMinutes;
        @Singular(value = "material", ignoreNullCollections = true)
        private Set<MaterialDto.Response> materials;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ServiceDto.Response buildServiceDtoResponse(Service service) {
            return MAPPER.convert(service);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @EqualsAndHashCode(callSuper = true)
    @Schema(name = "Service.Representation")
    public static class Representation extends RepresentationModel<ServiceDto.Representation> implements ServiceDef.Representation {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal cost;
        private Long averageTimeInMinutes;
        private LocalDateTime createdAt;

        public UUID getId() {
            add(linkTo(getControllerClass())
                    .slash(id)
                    .withSelfRel());
            return id;
        }

        public static ServiceDto.Representation buildServiceDtoRepresentation(Service customer) {
            return MAPPER.convertToRepresentation(customer);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Service.PutRequest")
    public static class PutRequest implements ServiceDef.PutRequest {
        private String name;
        private String description;
        private BigDecimal cost;
        @Singular(value = "materialId", ignoreNullCollections = true)
        private Set<UUID> materialsIds;

        public Service buildService() {
            return MAPPER.convert(this);
        }
    }
}
