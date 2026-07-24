package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.NotificationDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.assertions.EmailDtoAssertions.assertThat_EmailDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.EmailDtoAssertions.assertThat_EmailDto_Response;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class NotificationDtoAssertions {

    public static Response assertThat_NotificationDto_Response(NotificationDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final NotificationDto.Response actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.NotificationFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Notification() {
            // Self
            assertThat(actual.getId())
                    .hasToString("d515e9fb-7c82-4e7b-9991-797988ccfa64");
            assertThat(actual.getExternalId())
                    .hasToString("d2b16521-39ce-479c-b779-a9ed5238a6c3");

            // Composition
            assertThat_EmailDto_Response(actual.getEmail())
                    .wasConvertedFrom_Email();

            // Inheritance (AuditableTable context)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("15/12/2026 23:59:59"));
            assertThat(actual.getUpdatedAt())
                    .isEqualTo(newDateTime("16/12/2026 23:59:59"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }

    public static Representation assertThat_NotificationDto_Representation(NotificationDto.Representation actual) {
        assertThat(actual).isNotNull();
        return new Representation(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final NotificationDto.Representation actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.NotificationFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Notification() {
            // Self
            assertThat(actual.getId())
                    .hasToString("d515e9fb-7c82-4e7b-9991-797988ccfa64");
            assertThat(actual.getExternalId())
                    .hasToString("d2b16521-39ce-479c-b779-a9ed5238a6c3");

            // Composition
            assertThat_EmailDto_Representation(actual.getEmail())
                    .wasConvertedFrom_Email();

            // Inheritance (AuditableTable context)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("15/12/2026 23:59:59"));

            // HATEOAS
            assertThat(actual.getLinks())
                    .hasToString("</v1/notifications/d515e9fb-7c82-4e7b-9991-797988ccfa64>;rel=\"self\"");

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}