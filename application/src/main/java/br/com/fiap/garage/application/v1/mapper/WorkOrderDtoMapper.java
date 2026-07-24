package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.commons.map.CpfMap;
import br.com.fiap.garage.application.v1.dto.EstimatedMaterialDto;
import br.com.fiap.garage.application.v1.dto.EstimatedServiceDto;
import br.com.fiap.garage.application.v1.dto.WorkOrderDto;
import br.com.fiap.garage.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface WorkOrderDtoMapper extends CpfMap {

    @Mapping(target = "vehicle", source = "vehicleId")
    @Mapping(target = "employee", source = "employeeId")
    WorkOrder convert(WorkOrderDto.Request source);

    @Mapping(target = "employee.cpf", source = "employee.cpf", qualifiedByName = "formattedCpf")
    WorkOrderDto.Response convert(WorkOrder source);

    WorkOrderDto.Representation convertToRepresentation(WorkOrder source);

    @Mapping(target = "id", source = "serviceId")
    EstimatedServiceDto.Response convert(EstimatedService source);

    @Mapping(target = "id", source = "materialId")
    EstimatedMaterialDto.Response convert(EstimatedMaterial source);

    default Vehicle mapVehicle(UUID vehicleId) {
        if (vehicleId == null)
            return null;
        return Vehicle.builder()
                .id(vehicleId)
                .build();
    }

    default Employee mapEmployee(UUID employeeId) {
        if (employeeId == null)
            return null;
        return Employee.builder()
                .id(employeeId)
                .build();
    }
}
