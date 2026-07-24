package br.com.fiap.garage.application.v1.swagger;

import br.com.fiap.garage.application.v1.dto.ServiceDto;
import br.com.fiap.garage.domain.filter.ServiceFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Tag(name = "Service (v1)", description = "Service resource.")
public interface ServiceSwagger extends GenericSwagger {

    @Operation(summary = "Create Service.")
    @ApiResponse(responseCode = "201", description = "Created")
    ServiceDto.Response create(
            ServiceDto.Request requestBody);

    @Operation(summary = "Search Service by id.")
    @ApiResponse(responseCode = "200", description = "OK")
    ServiceDto.Response findById(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID serviceId);

    @Operation(summary = "Search all Services using filters.")
    @ApiResponse(responseCode = "200", description = "Ok")
    Page<ServiceDto.Representation> findAll(
            ServiceFilter filter);

    @Operation(summary = "Update Service.")
    @ApiResponse(responseCode = "200", description = "Ok")
    ServiceDto.Response update(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID serviceId,
            ServiceDto.PutRequest requestBody);

    @Operation(summary = "Calculate Services average time.")
    @ApiResponse(responseCode = "200", description = "Ok")
    void calculateAverageTime();
}
