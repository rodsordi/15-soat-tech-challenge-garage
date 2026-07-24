package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.garage.application.v1.dto.EmployeeDto;
import br.com.fiap.garage.application.v1.swagger.EmployeeSwagger;
import br.com.fiap.garage.domain.filter.EmployeeFilter;
import br.com.fiap.garage.domain.use_case.EmployeeCreationUseCase;
import br.com.fiap.garage.domain.use_case.EmployeeSearchUseCase;
import br.com.fiap.garage.domain.use_case.EmployeeUpdateUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.EmployeeDto.Response.buildEmployeeDtoResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/employees")
public class EmployeeController implements EmployeeSwagger {

    private final EmployeeCreationUseCase employeeCreationUseCase;

    private final EmployeeSearchUseCase employeeSearchUseCase;

    private final EmployeeUpdateUseCase employeeUpdateUseCase;

    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    @Override
    public EmployeeDto.Response create(
            @Valid
            @RequestBody
            EmployeeDto.Request requestBody) {
        var employee = requestBody.buildEmployee();
        var createdEmployee = employeeCreationUseCase.create(employee);
        return buildEmployeeDtoResponse(createdEmployee);
    }

    @GetMapping(path = "/{employeeId}",
            produces = APPLICATION_JSON_VALUE)
    @Override
    public EmployeeDto.Response findById(
            @PathVariable("employeeId")
            UUID employeeId) {
        var foundEmployee = employeeSearchUseCase.findById(employeeId);
        return buildEmployeeDtoResponse(foundEmployee);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Override
    public Page<EmployeeDto.Representation> findAll(
            EmployeeFilter filter) {
        var foundEmployees = employeeSearchUseCase.findAll(filter);
        var responseBody = foundEmployees.stream()
                .map(EmployeeDto.Representation::buildEmployeeDtoRepresentation)
                .toList();
        return new PageImpl<>(responseBody, filter.buildPageRequest(), responseBody.size());
    }

    @PatchMapping(path = "/{employeeId}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public EmployeeDto.Response update(
            @PathVariable("employeeId")
            UUID employeeId,
            @RequestBody
            @Valid
            EmployeeDto.PatchRequest requestBody) {
        var employee = requestBody.buildEmployee();
        var updatedEmployee = employeeUpdateUseCase.update(employeeId, employee);
        return buildEmployeeDtoResponse(updatedEmployee);
    }
}
