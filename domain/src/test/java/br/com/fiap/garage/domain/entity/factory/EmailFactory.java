package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.Email;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmailFactory {

    private final Email.EmailBuilder<?, ?> builder;

    public static EmailFactory create_Email() {
        return new EmailFactory(Email.builder());
    }

    public Email withAllFields() {
        var result = builder
                // Self
                .id(fromString("0238fa85-1cd8-457a-a6ea-4f61d8d2f366"))
                .recipient("customer@example.com")
                .bcc("audit@garage.fiap.com.br")
                .subject("Your Vehicle Update")
                .message("Your vehicle maintenance is complete and ready for pickup.")
                // Inheritance (AuditableEntity)
                .createdAt(newDateTime("15/12/2026 23:59:59"))
                .updatedAt(newDateTime("16/12/2026 23:59:59"))
                .build();

        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public Email withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public Email valid() {
        return builder
                .recipient("customer@example.com")
                .bcc("audit@garage.fiap.com.br")
                .subject("Your Vehicle Update")
                .message("Your vehicle maintenance is complete and ready for pickup.")
                .build();
    }

    public Email initiatedEmpty() {
        return builder.build();
    }
}