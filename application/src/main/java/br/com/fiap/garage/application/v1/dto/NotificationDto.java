package br.com.fiap.garage.application.v1.dto;

import br.com.fiap.garage.application.v1.controller.NotificationController;
import br.com.fiap.garage.application.v1.mapper.NotificationDtoMapper;
import br.com.fiap.garage.domain.entity.Notification;
import br.com.fiap.garage.application.adapter.def.NotificationDef;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@NoArgsConstructor(access = PRIVATE)
public final class NotificationDto {

    private static final NotificationDtoMapper MAPPER = getMapper(NotificationDtoMapper.class);

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Notification.Request")
    public static class Request implements NotificationDef.Request {

        private UUID externalId;
        private EmailDto.Request email;

        public Notification buildNotification() {
            return MAPPER.convert(this);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Notification.Response")
    public static class Response implements NotificationDef.Response {

        private UUID id;
        private UUID externalId;
        private EmailDto.Response email;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static NotificationDto.Response buildNotificationDtoResponse(Notification workOrder) {
            return MAPPER.convert(workOrder);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @EqualsAndHashCode(callSuper = true)
    @Schema(name = "Notification.Representation")
    public static class Representation extends RepresentationModel<Representation> implements NotificationDef.Representation {

        private UUID id;
        private UUID externalId;
        private EmailDto.Representation email;
        private LocalDateTime createdAt;

        public UUID getId() {
            add(linkTo(NotificationController.class)
                    .slash(id)
                    .withSelfRel());
            return id;
        }

        public static NotificationDto.Representation buildNotificationDtoRepresentation(Notification workOrder) {
            return MAPPER.convertToRepresentation(workOrder);
        }
    }
}
