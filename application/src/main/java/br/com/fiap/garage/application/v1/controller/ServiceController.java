package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.garage.application.v1.dto.ServiceDto;
import br.com.fiap.garage.application.v1.swagger.ServiceSwagger;
import br.com.fiap.garage.domain.filter.ServiceFilter;
import br.com.fiap.garage.domain.use_case.ServiceCreationUseCase;
import br.com.fiap.garage.domain.use_case.ServiceSearchUseCase;
import br.com.fiap.garage.domain.use_case.ServiceUpdateUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.ServiceDto.Response.buildServiceDtoResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/services")
public class ServiceController implements ServiceSwagger {

    private final ServiceCreationUseCase serviceCreationUseCase;

    private final ServiceSearchUseCase serviceSearchUseCase;

    private final ServiceUpdateUseCase serviceUpdateUseCase;

    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    @Override
    public ServiceDto.Response create(
            @Valid
            @RequestBody
            ServiceDto.Request requestBody) {
        var service = requestBody.buildService();
        var createdService = serviceCreationUseCase.create(service, requestBody.getMaterialsIds());
        return buildServiceDtoResponse(createdService);
    }

    @GetMapping(path = "/{serviceId}",
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ServiceDto.Response findById(
            @PathVariable("serviceId")
            UUID serviceId) {
        var foundService = serviceSearchUseCase.findById(serviceId);
        return buildServiceDtoResponse(foundService);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Override
    public Page<ServiceDto.Representation> findAll(
            ServiceFilter filter) {
        var foundServices = serviceSearchUseCase.findAll(filter);
        var responseBody = foundServices.stream()
                .map(ServiceDto.Representation::buildServiceDtoRepresentation)
                .toList();
        return new PageImpl<>(responseBody, filter.buildPageRequest(), responseBody.size());
    }

    @PutMapping(path = "/{serviceId}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ServiceDto.Response update(
            @PathVariable("serviceId")
            UUID serviceId,
            @RequestBody
            @Valid
            ServiceDto.PutRequest requestBody) {
        var service = requestBody.buildService();
        var updatedService = serviceUpdateUseCase.update(serviceId, requestBody.getMaterialsIds(), service);
        return buildServiceDtoResponse(updatedService);
    }

    @GetMapping(path = "/calculateAverageTime")
    @ResponseStatus(NO_CONTENT)
    @Override
    public void calculateAverageTime() {
        //TODO: Substituting a scheduler or batch job for a while //NOSONAR
        serviceUpdateUseCase.calculateAverageTime();
    }
}
