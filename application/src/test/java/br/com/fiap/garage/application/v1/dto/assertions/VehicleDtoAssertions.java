package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.VehicleDto;
import lombok.RequiredArgsConstructor;

import java.time.Year;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class VehicleDtoAssertions {

    public static Response assertThat_VehicleDto_Response(VehicleDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final VehicleDto.Response actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.VehicleFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Vehicle() {
            // Self
            assertThat(actual.getId())
                    .hasToString("9f8792ea-cf8f-43d1-824f-9f7bc433e404");
            assertThat(actual.getMake())
                    .isEqualTo("Toyota");
            assertThat(actual.getModel())
                    .isEqualTo("Corolla");
            assertThat(actual.getLicensePlate())
                    .isEqualTo("ABC1234");
            assertThat(actual.getManufactureYear())
                    .isEqualTo(Year.parse("2024"));

            // Composition
            assertThat(actual.getCustomer())
                    .isNull();

            //Inheritance (AuditableTable)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("21/04/2026 10:00:00"));
            assertThat(actual.getUpdatedAt())
                    .isEqualTo(newDateTime("21/04/2026 15:30:00"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }

    public static Representation assertThat_VehicleDto_Representation(VehicleDto.Representation actual) {
        assertThat(actual).isNotNull();
        return new Representation(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final VehicleDto.Representation actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.VehicleFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Vehicle() {
            // Self
            assertThat(actual.getId())
                    .hasToString("9f8792ea-cf8f-43d1-824f-9f7bc433e404");
            assertThat(actual.getMake())
                    .isEqualTo("Toyota");
            assertThat(actual.getModel())
                    .isEqualTo("Corolla");
            assertThat(actual.getLicensePlate())
                    .isEqualTo("ABC1234");
            assertThat(actual.getManufactureYear())
                    .isEqualTo(Year.parse("2024"));

            //Inheritance (AuditableTable)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("21/04/2026 10:00:00"));
            assertThat(actual.getLinks())
                    .hasToString("</v1/vehicles/9f8792ea-cf8f-43d1-824f-9f7bc433e404>;rel=\"self\"");

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}