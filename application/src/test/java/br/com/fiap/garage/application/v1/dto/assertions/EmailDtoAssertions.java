package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.EmailDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmailDtoAssertions {

    public static Response assertThat_EmailDto_Response(EmailDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final EmailDto.Response actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.EmailFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Email() {
            // Self
            assertThat(actual.getRecipient())
                    .isEqualTo("customer@example.com");
            assertThat(actual.getBcc())
                    .isEqualTo("audit@garage.fiap.com.br");
            assertThat(actual.getSubject())
                    .isEqualTo("Your Vehicle Update");
            assertThat(actual.getMessage())
                    .isEqualTo("Your vehicle maintenance is complete and ready for pickup.");

            // Inheritance (AuditableEntity context)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("15/12/2026 23:59:59"));
            assertThat(actual.getUpdatedAt())
                    .isEqualTo(newDateTime("16/12/2026 23:59:59"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }

    public static Representation assertThat_EmailDto_Representation(EmailDto.Representation actual) {
        assertThat(actual).isNotNull();
        return new Representation(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final EmailDto.Representation actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.EmailFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Email() {
            // Self
            assertThat(actual.getRecipient())
                    .isEqualTo("customer@example.com");
            assertThat(actual.getSubject())
                    .isEqualTo("Your Vehicle Update");

            // Inheritance (AuditableEntity context)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("15/12/2026 23:59:59"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}