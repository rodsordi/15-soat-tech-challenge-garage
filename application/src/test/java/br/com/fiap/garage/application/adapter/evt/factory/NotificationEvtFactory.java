package br.com.fiap.garage.application.adapter.evt.factory;

import br.com.fiap.garage.application.adapter.evt.NotificationEvt;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.adapter.evt.factory.EmailEvtFactory.create_EmailEvt;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class NotificationEvtFactory {

    private final NotificationEvt.NotificationEvtBuilder builder;

    public static NotificationEvtFactory create_NotificationEvt() {
        return new NotificationEvtFactory(NotificationEvt.builder());
    }

    public NotificationEvt withAllFields() {
        var result = builder
                .externalId(fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .email(create_EmailEvt().withAllFields())
                .build();

        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public NotificationEvt valid() {
        return builder
                .externalId(fromString("a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"))
                .email(create_EmailEvt().valid())
                .build();
    }

    public NotificationEvt initiatedEmpty() {
        return builder.build();
    }
}