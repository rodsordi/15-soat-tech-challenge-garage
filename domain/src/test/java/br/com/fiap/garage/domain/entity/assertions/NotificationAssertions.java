package br.com.fiap.garage.domain.entity.assertions;

import br.com.fiap.garage.domain.entity.Notification;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.assertions.EmailAssertions.assertThat_Email;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class NotificationAssertions {

    private final Notification actual;

    public static NotificationAssertions assertThat_Notification(Notification actual) {
        assertThat(actual).isNotNull();
        return new NotificationAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.domain.entity.factory.NotificationFactory
     * .withAllFields()
     */
    public void isEqualTo_Notification() {
        // Self
        assertThat(actual.getId())
                .hasToString("a7c8e9d0-1234-4567-890a-bcdef1234567");
        assertThat(actual.getExternalId())
                .hasToString("86dee474-1fd9-4e21-bd53-2b4018a0c01e");

        // Composition
        assertThat_Email(actual.getEmail())
                .isEqualTo_Email();

        // Inheritance (AuditableEntity)
        assertThat(actual.getCreatedAt())
                .isEqualTo(newDateTime("27/04/2026 10:00:00"));
        assertThat(actual.getUpdatedAt())
                .isEqualTo(newDateTime("27/04/2026 11:30:00"));

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }

    /**
     * @see br.com.fiap.garage.application.v1.dto.factory.NotificationDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_NotificationDto_Request() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getExternalId())
                .hasToString("c38d7c09-e064-43ea-ba87-f7ff76113e34");

        // Composition
        assertThat_Email(actual.getEmail())
                .wasConvertedFrom_EmailDto_Request();

        // Inheritance (AuditableEntity)
        assertThat(actual.getCreatedAt())
                .isNull();
        assertThat(actual.getUpdatedAt())
                .isNull();

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }
}