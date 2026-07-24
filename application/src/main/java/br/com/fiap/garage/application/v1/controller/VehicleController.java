package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.garage.application.v1.dto.VehicleDto;
import br.com.fiap.garage.application.v1.swagger.VehicleSwagger;
import br.com.fiap.garage.domain.filter.VehicleFilter;
import br.com.fiap.garage.domain.use_case.VehicleSearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.VehicleDto.Response.buildVehicleDtoResponse;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/vehicles")
public class VehicleController implements VehicleSwagger {

    private final VehicleSearchUseCase vehicleSearchUseCase;

    @GetMapping(path = "/{vehicleId}",
            produces = APPLICATION_JSON_VALUE)
    @Override
    public VehicleDto.Response findById(
            @PathVariable("vehicleId")
            UUID vehicleId) {
        var foundVehicle = vehicleSearchUseCase.findById(vehicleId);
        return buildVehicleDtoResponse(foundVehicle);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Override
    public Page<VehicleDto.Representation> findAll(
            VehicleFilter filter) {
        var foundVehicles = vehicleSearchUseCase.findAll(filter);
        var responseBody = foundVehicles.stream()
                .map(VehicleDto.Representation::buildVehicleDtoRepresentation)
                .toList();
        return new PageImpl<>(responseBody, filter.buildPageRequest(), responseBody.size());
    }
}
