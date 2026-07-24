package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.commons.map.DocumentMap;
import br.com.fiap.garage.application.v1.dto.CustomerDto;
import br.com.fiap.garage.domain.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface CustomerDtoMapper extends DocumentMap {

    @Mapping(target = "document", qualifiedByName = "unformattedDocument")
    Customer convert(CustomerDto.Request source);

    Customer convert(CustomerDto.PatchRequest source);

    @Mapping(target = "document", qualifiedByName = "formattedDocument")
    CustomerDto.Response convert(Customer source);

    @Mapping(target = "document", qualifiedByName = "formattedDocument")
    CustomerDto.Representation convertToRepresentation(Customer source);
}
