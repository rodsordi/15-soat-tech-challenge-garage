package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.garage.application.v1.dto.VehicleDto;
import br.com.fiap.garage.application.v1.swagger.NestedVehicleSwagger;
import br.com.fiap.garage.domain.use_case.VehicleCreationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.VehicleDto.Response.buildVehicleDtoResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/customers/{customerId}/vehicles")
public class NestedVehicleController implements NestedVehicleSwagger {

    private final VehicleCreationUseCase vehicleCreationUseCase;

    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    @Override
    public VehicleDto.Response create(
            @PathVariable
            UUID customerId,
            @Valid
            @RequestBody
            VehicleDto.Request requestBody) {
        var vehicle = requestBody.buildVehicle();
        var createdVehicle = vehicleCreationUseCase.create(customerId, vehicle);
        return buildVehicleDtoResponse(createdVehicle);
    }
}
