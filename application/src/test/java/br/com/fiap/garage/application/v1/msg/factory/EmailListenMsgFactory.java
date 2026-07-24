package br.com.fiap.garage.application.v1.msg.factory;

import br.com.fiap.garage.application.v1.msg.EmailListenMsg;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmailListenMsgFactory {

    private final EmailListenMsg.EmailListenMsgBuilder builder;

    public static EmailListenMsgFactory create_EmailListenMsgFactory() {
        return new EmailListenMsgFactory(EmailListenMsg.builder());
    }

    public EmailListenMsg withAllFields() {
        var result = builder
                .recipient("jonh.doe@email.com")
                .subject("Subject xpto")
                .message("A very important message")
                .build();
        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }
}