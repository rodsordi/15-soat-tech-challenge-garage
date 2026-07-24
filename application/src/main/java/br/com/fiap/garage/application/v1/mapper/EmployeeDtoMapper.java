package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.commons.map.CpfMap;
import br.com.fiap.garage.application.v1.dto.EmployeeDto;
import br.com.fiap.garage.domain.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface EmployeeDtoMapper extends CpfMap {

    @Mapping(target = "cpf", qualifiedByName = "unformattedCpf")
    Employee convert(EmployeeDto.Request source);

    Employee convert(EmployeeDto.PatchRequest source);

    @Mapping(target = "cpf", qualifiedByName = "formattedCpf")
    EmployeeDto.Response convert(Employee source);

    @Mapping(target = "cpf", qualifiedByName = "formattedCpf")
    EmployeeDto.Representation convertToRepresentation(Employee source);
}
