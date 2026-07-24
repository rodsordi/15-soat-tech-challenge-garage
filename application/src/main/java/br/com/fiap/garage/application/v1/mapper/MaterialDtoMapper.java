package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.garage.application.v1.dto.MaterialDto;
import br.com.fiap.garage.domain.entity.Material;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface MaterialDtoMapper {

    Material convert(MaterialDto.Request source);

    MaterialDto.Response convert(Material source);

    MaterialDto.Representation convertToRepresentation(Material source);
}
