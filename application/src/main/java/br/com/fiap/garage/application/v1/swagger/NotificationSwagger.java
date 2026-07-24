package br.com.fiap.garage.application.v1.swagger;

import br.com.fiap.garage.application.v1.dto.NotificationDto;
import br.com.fiap.garage.domain.filter.NotificationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Tag(name = "Notification (v1)", description = "Notification resource.")
public interface NotificationSwagger extends GenericSwagger {

    @Operation(summary = "Create Notification.")
    @ApiResponse(responseCode = "201", description = "Created")
    NotificationDto.Response create(
            NotificationDto.Request requestBody);

    @Operation(summary = "Search Notification by id.")
    @ApiResponse(responseCode = "200", description = "OK")
    NotificationDto.Response findById(
            @Parameter(example = "8ed33428-faea-40fb-b913-7d591cfc0e2a")
            UUID notificationId);

    @Operation(summary = "Search all Notifications using filters.")
    @ApiResponse(responseCode = "200", description = "Ok")
    Page<NotificationDto.Representation> findAll(
            NotificationFilter filter);
}
