package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.garage.application.v1.dto.ServiceDto;
import br.com.fiap.garage.domain.entity.Service;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface ServiceDtoMapper {

    Service convert(ServiceDto.Request source);

    Service convert(ServiceDto.PutRequest source);

    ServiceDto.Response convert(Service source);

    ServiceDto.Representation convertToRepresentation(Service source);
}
