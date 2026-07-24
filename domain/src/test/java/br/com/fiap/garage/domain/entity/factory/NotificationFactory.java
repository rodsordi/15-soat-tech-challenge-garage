package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.Notification;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.EmailFactory.create_Email;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class NotificationFactory {

    private final Notification.NotificationBuilder<?, ?> builder;

    public static NotificationFactory create_Notification() {
        return new NotificationFactory(Notification.builder());
    }

    public Notification withAllFields() {
        var result = builder
                // Self
                .id(fromString("d515e9fb-7c82-4e7b-9991-797988ccfa64"))
                .externalId(UUID.fromString("d2b16521-39ce-479c-b779-a9ed5238a6c3"))
                // Composition
                .email(create_Email().withAllFields())
                // Inheritance (AuditableEntity)
                .createdAt(newDateTime("15/12/2026 23:59:59"))
                .updatedAt(newDateTime("16/12/2026 23:59:59"))
                .build();

        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public Notification withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .email(create_Email().withAllFieldsExceptDB())
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public Notification valid() {
        return builder
                .email(create_Email().valid())
                .build();
    }

    public Notification initiatedEmpty() {
        return builder
                .email(create_Email().initiatedEmpty())
                .build();
    }
}