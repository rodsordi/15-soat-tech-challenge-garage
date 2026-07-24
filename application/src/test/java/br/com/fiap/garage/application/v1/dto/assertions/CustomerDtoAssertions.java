package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.CustomerDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.assertions.VehicleDtoAssertions.assertThat_VehicleDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.VehicleDtoAssertions.assertThat_VehicleDto_Response;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class CustomerDtoAssertions {

    public static Response assertThat_CustomerDto_Response(CustomerDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {
        
        private final CustomerDto.Response actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.CustomerFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Customer() {
            // Self
            assertThat(actual.getId())
                    .hasToString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
            assertThat(actual.getUsername())
                    .isEqualTo("jack.doe@company.com");
            assertThat(actual.getName())
                    .isEqualTo("John Doe");
            assertThat(actual.getEmail())
                    .isEqualTo("john.doe@fiap.com.br");
            assertThat(actual.getDocument())
                    .isEqualTo("27.351.626/0001-07");

            // Composition
            assertThat_VehicleDto_Response(actual.getVehicles().stream().findFirst().orElseThrow())
                    .wasConvertedFrom_Vehicle();

            //Inheritance (AuditableTable)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));
            assertThat(actual.getUpdatedAt())
                    .isEqualTo(newDateTime("14/12/2026 23:59:59"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }

    public static Representation assertThat_CustomerDto_Representation(CustomerDto.Representation actual) {
        assertThat(actual).isNotNull();
        return new Representation(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final CustomerDto.Representation actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.CustomerFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Customer() {
            // Self
            assertThat(actual.getId())
                    .hasToString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
            assertThat(actual.getUsername())
                    .isEqualTo("jack.doe@company.com");
            assertThat(actual.getName())
                    .isEqualTo("John Doe");
            assertThat(actual.getEmail())
                    .isEqualTo("john.doe@fiap.com.br");
            assertThat(actual.getDocument())
                    .isEqualTo("27.351.626/0001-07");

            //Inheritance (AuditableTable)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));
            assertThat(actual.getLinks())
                    .hasToString("</v1/customers/f47ac10b-58cc-4372-a567-0e02b2c3d479>;rel=\"self\"");

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}