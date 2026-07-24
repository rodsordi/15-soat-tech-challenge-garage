package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.garage.application.v1.msg.NotificationMsg;
import br.com.fiap.garage.domain.entity.Notification;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface NotificationMsgMapper {

    Notification convert(NotificationMsg source);
}
