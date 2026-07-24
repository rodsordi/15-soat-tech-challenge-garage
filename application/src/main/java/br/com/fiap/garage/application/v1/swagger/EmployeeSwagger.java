package br.com.fiap.garage.application.v1.swagger;

import br.com.fiap.garage.application.v1.dto.EmployeeDto;
import br.com.fiap.garage.domain.filter.EmployeeFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Tag(name = "Employee (v1)", description = "Employee resource.")
public interface EmployeeSwagger extends GenericSwagger {

    EmployeeDto.Response create(
            EmployeeDto.Request requestBody);

    EmployeeDto.Response findById(
            UUID employeeId);

    Page<EmployeeDto.Representation> findAll(
            EmployeeFilter filter);

    EmployeeDto.Response update(
            UUID employeeId,
            EmployeeDto.PatchRequest requestBody);
}
