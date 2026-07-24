package br.com.fiap.garage.application.v1.dto;

import br.com.fiap.garage.application.v1.def.WorkOrderDef;
import br.com.fiap.garage.application.v1.mapper.WorkOrderDtoMapper;
import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
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
public final class WorkOrderDto {

    private static final WorkOrderDtoMapper MAPPER = getMapper(WorkOrderDtoMapper.class);

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "WorkOrder.Request")
    public static class Request implements WorkOrderDef.Request {
        private UUID vehicleId;
        private UUID employeeId;
        @Singular(value = "serviceId", ignoreNullCollections = true)
        private Set<UUID> servicesIds;

        public WorkOrder buildWorkOrder() {
            return MAPPER.convert(this);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "WorkOrder.Response")
    public static class Response implements WorkOrderDef.Response {
        private UUID id;
        private WorkOrderStatus status;
        private BigDecimal totalAmount;
        private VehicleDto.Representation vehicle;
        private EmployeeDto.Representation employee;
        @Singular(value = "service", ignoreNullCollections = true)
        private Set<EstimatedServiceDto.Response> estimatedServices;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static WorkOrderDto.Response buildWorkOrderDtoResponse(WorkOrder workOrder) {
            return MAPPER.convert(workOrder);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @EqualsAndHashCode(callSuper = true)
    @Schema(name = "WorkOrder.Representation")
    public static class Representation extends RepresentationModel<Representation> implements WorkOrderDef.Representation {
        private UUID id;
        private WorkOrderStatus status;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;

        public UUID getId() {
            add(linkTo(getControllerClass())
                    .slash(id)
                    .withSelfRel());
            return id;
        }

        public static WorkOrderDto.Representation buildWorkOrderDtoRepresentation(WorkOrder workOrder) {
            return MAPPER.convertToRepresentation(workOrder);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "WorkOrder.PatchRequest")
    public static class PatchRequest implements WorkOrderDef.PatchRequest {
        private WorkOrderStatus status;
        private UUID employeeId;
        private UUID finishedServiceId;
    }
}
