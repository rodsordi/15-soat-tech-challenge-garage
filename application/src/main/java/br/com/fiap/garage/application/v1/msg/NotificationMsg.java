package br.com.fiap.garage.application.v1.msg;

import br.com.fiap.garage.application.adapter.def.NotificationDef;
import lombok.*;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Getter(onMethod_ = @Override)
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@Builder
public class NotificationMsg implements NotificationDef.Request {

    private UUID externalId;

    private EmailListenMsg email;
}
