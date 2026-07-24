package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.garage.application.v1.dto.NotificationDto;
import br.com.fiap.garage.application.v1.swagger.NotificationSwagger;
import br.com.fiap.garage.domain.filter.NotificationFilter;
import br.com.fiap.garage.domain.use_case.NotificationCreationUseCase;
import br.com.fiap.garage.domain.use_case.NotificationSearchUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.NotificationDto.Response.buildNotificationDtoResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/notifications")
public class NotificationController implements NotificationSwagger {

    private final NotificationCreationUseCase notificationCreationUseCase;

    private final NotificationSearchUseCase notificationSearchUseCase;

    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    @Override
    public NotificationDto.Response create(
            @Valid
            @RequestBody
            NotificationDto.Request requestBody) {
        var notification = requestBody.buildNotification();
        var createdNotification = notificationCreationUseCase.create(notification);
        return buildNotificationDtoResponse(createdNotification);
    }

    @GetMapping(path = "/{notificationId}",
            produces = APPLICATION_JSON_VALUE)
    @Override
    public NotificationDto.Response findById(
            @PathVariable("notificationId")
            UUID notificationId) {
        var foundNotification = notificationSearchUseCase.findById(notificationId);
        return buildNotificationDtoResponse(foundNotification);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Override
    public Page<NotificationDto.Representation> findAll(
            NotificationFilter filter) {
        var foundNotifications = notificationSearchUseCase.findAll(filter);
        var responseBody = foundNotifications.stream()
                .map(NotificationDto.Representation::buildNotificationDtoRepresentation)
                .toList();
        return new PageImpl<>(responseBody, filter.buildPageRequest(), responseBody.size());
    }
}
