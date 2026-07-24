package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.ServiceDto;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.assertions.MaterialDtoAssertions.assertThat_MaterialDto_Response;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class ServiceDtoAssertions {

    public static Response assertThat_ServiceDto_Response(ServiceDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final ServiceDto.Response actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.ServiceFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Service() {
            // Self
            assertThat(actual.getId())
                    .hasToString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
            assertThat(actual.getName())
                    .isEqualTo("Complete Engine Overhaul");
            assertThat(actual.getDescription())
                    .isEqualTo("Comprehensive engine inspection, repair, and parts replacement");
            assertThat(actual.getCost())
                    .isEqualTo(new BigDecimal("3500.00"));
            assertThat(actual.getAverageTimeInMinutes())
                    .isZero();

            // Composition
            assertThat(actual.getMaterials())
                    .hasSize(1);
            var material = actual.getMaterials()
                    .stream()
                    .findFirst()
                    .orElseThrow();
            assertThat_MaterialDto_Response(material)
                    .wasConvertedFrom_Material();

            // Inheritance (AuditableTable / AuditableEntity)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));
            assertThat(actual.getUpdatedAt())
                    .isEqualTo(newDateTime("14/12/2026 23:59:59"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }

    public static Representation assertThat_ServiceDto_Representation(ServiceDto.Representation actual) {
        assertThat(actual).isNotNull();
        return new Representation(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final ServiceDto.Representation actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.ServiceFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Service() {
            // Self
            assertThat(actual.getId())
                    .hasToString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
            assertThat(actual.getName())
                    .isEqualTo("Complete Engine Overhaul");
            assertThat(actual.getDescription())
                    .isEqualTo("Comprehensive engine inspection, repair, and parts replacement");
            assertThat(actual.getCost())
                    .isEqualTo(new BigDecimal("3500.00"));
            assertThat(actual.getAverageTimeInMinutes())
                    .isZero();

            // Inheritance (AuditableTable / AuditableEntity)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));

            // HATEOAS
            assertThat(actual.getLinks())
                    .hasToString("</v1/services/f47ac10b-58cc-4372-a567-0e02b2c3d479>;rel=\"self\"");

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}