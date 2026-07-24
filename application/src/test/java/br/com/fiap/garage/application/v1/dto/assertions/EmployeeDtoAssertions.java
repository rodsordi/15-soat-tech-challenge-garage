package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmployeeDtoAssertions {

    public static Response assertThat_EmployeeDto_Response(EmployeeDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {
        
        private final EmployeeDto.Response actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.EmployeeFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Employee() {
            // Self
            assertThat(actual.getId())
                    .hasToString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");
            assertThat(actual.getUsername())
                    .isEqualTo("john.doe@garage.com");
            assertThat(actual.getName())
                    .isEqualTo("John Doe");
            assertThat(actual.getEmail())
                    .isEqualTo("john.doe@garage.com");
            assertThat(actual.getCpf())
                    .isEqualTo("179.026.520-75");


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

    public static Representation assertThat_EmployeeDto_Representation(EmployeeDto.Representation actual) {
        assertThat(actual).isNotNull();
        return new Representation(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final EmployeeDto.Representation actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.EmployeeFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Employee() {
            // Self
            assertThat(actual.getId())
                    .hasToString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");
            assertThat(actual.getUsername())
                    .isEqualTo("john.doe@garage.com");
            assertThat(actual.getName())
                    .isEqualTo("John Doe");
            assertThat(actual.getEmail())
                    .isEqualTo("john.doe@garage.com");
            assertThat(actual.getCpf())
                    .isEqualTo("179.026.520-75");

            //Inheritance (AuditableTable)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));
            assertThat(actual.getLinks())
                    .hasToString("</v1/employees/a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d>;rel=\"self\"");

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}