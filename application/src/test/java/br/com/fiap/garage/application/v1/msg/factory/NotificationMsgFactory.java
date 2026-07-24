package br.com.fiap.garage.application.v1.msg.factory;

import br.com.fiap.garage.application.v1.msg.NotificationMsg;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.msg.factory.EmailListenMsgFactory.create_EmailListenMsgFactory;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class NotificationMsgFactory {

    private final NotificationMsg.NotificationMsgBuilder builder;

    public static NotificationMsgFactory create_NotificationMsgFactory() {
        return new NotificationMsgFactory(NotificationMsg.builder());
    }

    public NotificationMsg withAllFields() {
        var result = builder
                .externalId(UUID.fromString("f882d31e-dcd0-4120-9b77-5a34239fa4f6"))
                .email(create_EmailListenMsgFactory()
                        .withAllFields())
                .build();
        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }
}