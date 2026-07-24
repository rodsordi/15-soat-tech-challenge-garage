package br.com.fiap.garage.application.v1.swagger;

import br.com.fiap.garage.application.v1.dto.WorkOrderDto;
import br.com.fiap.garage.domain.filter.WorkOrderFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Tag(name = "WorkOrder (v1)", description = "WorkOrder resource.")
public interface WorkOrderSwagger extends GenericSwagger {

    @Operation(summary = "Create WorkOrder.")
    @ApiResponse(responseCode = "201", description = "Created")
    WorkOrderDto.Response create(
            WorkOrderDto.Request requestBody);

    @Operation(summary = "Search WorkOrder by id.")
    @ApiResponse(responseCode = "200", description = "OK")
    WorkOrderDto.Response findById(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID workOrderId);

    @Operation(summary = "Search all WorkOrders using filters.")
    @ApiResponse(responseCode = "200", description = "Ok")
    Page<WorkOrderDto.Representation> findAll(
            WorkOrderFilter filter);

    @Operation(summary = "Update WorkOrder.")
    @ApiResponse(responseCode = "200", description = "Ok")
    WorkOrderDto.Response update(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID workOrderId,
            WorkOrderDto.PatchRequest requestBody);
}
