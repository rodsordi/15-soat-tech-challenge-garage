package br.com.fiap.garage.application.v1.swagger;

import br.com.fiap.garage.application.v1.dto.VehicleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Tag(name = "Vehicle (v1)", description = "Vehicle resource.")
public interface NestedVehicleSwagger extends GenericSwagger {

    @Operation(summary = "Create Vehicle.")
    @ApiResponse(responseCode = "201", description = "Created")
    VehicleDto.Response create(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID customerId,
            VehicleDto.Request requestBody);
}
