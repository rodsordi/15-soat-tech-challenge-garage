package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.garage.application.v1.dto.VehicleDto;
import br.com.fiap.garage.domain.entity.Vehicle;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface VehicleDtoMapper {

    Vehicle convert(VehicleDto.Request source);

    VehicleDto.Response convert(Vehicle source);

    VehicleDto.Representation convertToRepresentation(Vehicle source);
}
