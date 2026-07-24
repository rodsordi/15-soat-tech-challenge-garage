package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.garage.application.v1.dto.WorkOrderDto;
import br.com.fiap.garage.application.v1.swagger.WorkOrderSwagger;
import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.filter.WorkOrderFilter;
import br.com.fiap.garage.domain.use_case.WorkOrderCreationUseCase;
import br.com.fiap.garage.domain.use_case.WorkOrderSearchUseCase;
import br.com.fiap.garage.domain.use_case.WorkOrderUpdateUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.WorkOrderDto.Response.buildWorkOrderDtoResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/work-orders")
public class WorkOrderController implements WorkOrderSwagger {

    private final WorkOrderCreationUseCase workOrderCreationUseCase;

    private final WorkOrderSearchUseCase workOrderSearchUseCase;

    private final WorkOrderUpdateUseCase workOrderUpdateUseCase;

    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    @Override
    public WorkOrderDto.Response create(
            @Valid
            @RequestBody
            WorkOrderDto.Request requestBody) {
        var workOrder = requestBody.buildWorkOrder();
        var createdWorkOrder = workOrderCreationUseCase.create(workOrder, requestBody.getServicesIds());
        return buildWorkOrderDtoResponse(createdWorkOrder);
    }

    @GetMapping(path = "/{workOrderId}",
            produces = APPLICATION_JSON_VALUE)
    @Override
    public WorkOrderDto.Response findById(
            @PathVariable("workOrderId")
            UUID workOrderId) {
        var foundWorkOrder = workOrderSearchUseCase.findById(workOrderId);
        return buildWorkOrderDtoResponse(foundWorkOrder);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Override
    public Page<WorkOrderDto.Representation> findAll(
            WorkOrderFilter filter) {
        var foundWorkOrders = workOrderSearchUseCase.findAll(filter);
        var responseBody = foundWorkOrders.stream()
                .map(WorkOrderDto.Representation::buildWorkOrderDtoRepresentation)
                .toList();
        return new PageImpl<>(responseBody, filter.buildPageRequest(), responseBody.size());
    }

    @PatchMapping(path = "/{workOrderId}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public WorkOrderDto.Response update(
            @PathVariable("workOrderId")
            UUID workOrderId,
            @RequestBody
            @Valid
            WorkOrderDto.PatchRequest requestBody) {

        WorkOrder updatedWorkOrder = null;

        if (requestBody.getFinishedServiceId() != null)
            updatedWorkOrder = workOrderUpdateUseCase.finishService(workOrderId, requestBody.getFinishedServiceId());

        if (requestBody.getEmployeeId() != null)
            updatedWorkOrder = workOrderUpdateUseCase.updateEmployee(workOrderId, requestBody.getEmployeeId());

        if (requestBody.getStatus() != null)
            updatedWorkOrder = workOrderUpdateUseCase.updateStatus(workOrderId, requestBody.getStatus());

        return buildWorkOrderDtoResponse(updatedWorkOrder);
    }
}
