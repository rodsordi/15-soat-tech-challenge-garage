package br.com.fiap.garage.application.v1.swagger;

import br.com.fiap.garage.application.v1.dto.InventoryMaterialDto;
import br.com.fiap.garage.domain.filter.InventoryMaterialFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Tag(name = "Inventory (v1)", description = "Inventory resource.")
public interface InventoryMaterialSwagger extends GenericSwagger {

    @Operation(summary = "Create InventoryMaterial.")
    @ApiResponse(responseCode = "201", description = "Created")
    InventoryMaterialDto.Response create(
            InventoryMaterialDto.Request requestBody);

    @Operation(summary = "Search InventoryMaterial by id.")
    @ApiResponse(responseCode = "200", description = "OK")
    InventoryMaterialDto.Response findById(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID inventoryMaterialId);

    @Operation(summary = "Search all InventoryMaterials using filters.")
    @ApiResponse(responseCode = "200", description = "Ok")
    Page<InventoryMaterialDto.Representation> findAll(
            InventoryMaterialFilter filter);

    @Operation(summary = "Update InventoryMaterial.")
    @ApiResponse(responseCode = "200", description = "Ok")
    InventoryMaterialDto.Response update(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID inventoryMaterialId,
            InventoryMaterialDto.PutRequest requestBody);

    @Operation(summary = "Update InventoryMaterial.")
    @ApiResponse(responseCode = "200", description = "Ok")
    InventoryMaterialDto.Response update(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID inventoryMaterialId,
            InventoryMaterialDto.PatchRequest requestBody);
}
