package br.com.fiap.garage.application.v1.swagger;

import br.com.fiap.garage.application.v1.dto.VehicleDto;
import br.com.fiap.garage.domain.filter.VehicleFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Tag(name = "Vehicle (v1)", description = "Vehicle resource.")
public interface VehicleSwagger extends GenericSwagger {

    @Operation(summary = "Search Vehicle by id.")
    @ApiResponse(responseCode = "200", description = "OK")
    VehicleDto.Response findById(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID vehicleId);

    @Operation(summary = "Search all Vehicles using filters.")
    @ApiResponse(responseCode = "200", description = "Ok")
    Page<VehicleDto.Representation> findAll(
            VehicleFilter filter);
}
