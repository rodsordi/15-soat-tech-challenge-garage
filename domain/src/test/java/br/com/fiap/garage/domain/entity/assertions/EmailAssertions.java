package br.com.fiap.garage.domain.entity.assertions;

import br.com.fiap.garage.domain.entity.Email;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmailAssertions {

    private final Email actual;

    public static EmailAssertions assertThat_Email(Email actual) {
        assertThat(actual).isNotNull();
        return new EmailAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.domain.entity.factory.EmailFactory
     * .withAllFields()
     */
    public void isEqualTo_Email() {
        // Self
        assertThat(actual.getId())
                .hasToString("7f000001-8b2d-1a2b-818b-2d1a2b810000");
        assertThat(actual.getRecipient())
                .isEqualTo("customer@example.com");
        assertThat(actual.getBcc())
                .isEqualTo("archive@garage.com");
        assertThat(actual.getSubject())
                .isEqualTo("Service Update");
        assertThat(actual.getMessage())
                .isEqualTo("Your vehicle is ready for pickup.");

        // Inheritance (AuditableEntity)
        assertThat(actual.getCreatedAt())
                .isEqualTo(newDateTime("27/04/2026 10:00:00"));
        assertThat(actual.getUpdatedAt())
                .isEqualTo(newDateTime("27/04/2026 10:00:00"));

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }

    /**
     * @see br.com.fiap.garage.application.v1.dto.factory.EmailDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_EmailDto_Request() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getRecipient())
                .isEqualTo("customer@example.com");
        assertThat(actual.getBcc())
                .isNull();
        assertThat(actual.getSubject())
                .isEqualTo("Service Update");
        assertThat(actual.getMessage())
                .isEqualTo("Your vehicle is ready for pickup.");

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