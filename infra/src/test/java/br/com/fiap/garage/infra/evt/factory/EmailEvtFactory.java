package br.com.fiap.garage.infra.evt.factory;

import br.com.fiap.garage.infra.evt.EmailEvt;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmailEvtFactory {

    private final EmailEvt.EmailEvtBuilder builder;

    public static EmailEvtFactory create_EmailEvt() {
        return new EmailEvtFactory(EmailEvt.builder());
    }

    public EmailEvt withAllFields() {
        var result = builder
                .recipient("john.doe@example.com")
                .subject("System Notification")
                .message("Your service order has been updated successfully.")
                .build();

        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public EmailEvt valid() {
        return builder
                .recipient("john.doe@example.com")
                .subject("System Notification")
                .message("Your service order has been updated successfully.")
                .build();
    }

    public EmailEvt initiatedEmpty() {
        return builder.build();
    }
}