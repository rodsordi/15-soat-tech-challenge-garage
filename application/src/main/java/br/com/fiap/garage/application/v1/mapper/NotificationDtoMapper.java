package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.garage.application.v1.dto.NotificationDto;
import br.com.fiap.garage.domain.entity.Notification;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface NotificationDtoMapper {

    Notification convert(NotificationDto.Request source);

    NotificationDto.Response convert(Notification source);

    NotificationDto.Representation convertToRepresentation(Notification source);
}
