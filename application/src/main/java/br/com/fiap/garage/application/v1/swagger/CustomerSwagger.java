package br.com.fiap.garage.application.v1.swagger;

import br.com.fiap.garage.application.v1.dto.CustomerDto;
import br.com.fiap.garage.domain.filter.CustomerFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Tag(name = "Customer (v1)", description = "Customer resource.")
public interface CustomerSwagger extends GenericSwagger {

    @Operation(summary = "Create Customer.")
    @ApiResponse(responseCode = "201", description = "Created")
    CustomerDto.Response create(
            CustomerDto.Request requestBody);

    @Operation(summary = "Search Customer by id.")
    @ApiResponse(responseCode = "200", description = "OK")
    CustomerDto.Response findById(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID customerId);

    @Operation(summary = "Search all Customers using filters.")
    @ApiResponse(responseCode = "200", description = "Ok")
    Page<CustomerDto.Representation> findAll(
            CustomerFilter filter);

    @Operation(summary = "Update Customer.")
    @ApiResponse(responseCode = "200", description = "Ok")
    CustomerDto.Response update(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID customerId,
            CustomerDto.PatchRequest requestBody);
}
