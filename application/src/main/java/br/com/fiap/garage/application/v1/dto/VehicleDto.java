package br.com.fiap.garage.application.v1.dto;

import br.com.fiap.garage.application.v1.def.VehicleDef;
import br.com.fiap.garage.application.v1.mapper.VehicleDtoMapper;
import br.com.fiap.garage.domain.entity.Vehicle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@NoArgsConstructor(access = PRIVATE)
public final class VehicleDto {

    private static final VehicleDtoMapper MAPPER = getMapper(VehicleDtoMapper.class);

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Vehicle.Request")
    public static class Request implements VehicleDef.Request {
        private String make;
        private String model;
        private String licensePlate;
        private Year manufactureYear;

        public Vehicle buildVehicle() {
            return MAPPER.convert(this);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Vehicle.Response")
    public static class Response implements VehicleDef.Response {
        private UUID id;
        private String make;
        private String model;
        private String licensePlate;
        private Year manufactureYear;
        private CustomerDto.Representation customer;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static VehicleDto.Response buildVehicleDtoResponse(Vehicle vehicle) {
            return MAPPER.convert(vehicle);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @EqualsAndHashCode(callSuper = true)
    @Schema(name = "Vehicle.Representation")
    public static class Representation extends RepresentationModel<Representation> implements VehicleDef.Representation {
        private UUID id;
        private String make;
        private String model;
        private String licensePlate;
        private Year manufactureYear;
        private LocalDateTime createdAt;

        public UUID getId() {
            add(linkTo(getControllerClass())
                    .slash(id)
                    .withSelfRel());
            return id;
        }

        public static VehicleDto.Representation buildVehicleDtoRepresentation(Vehicle vehicle) {
            return MAPPER.convertToRepresentation(vehicle);
        }
    }
}
