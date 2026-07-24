package br.com.fiap.garage.application.v1.dto.factory;

import br.com.fiap.garage.application.v1.dto.CustomerDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.factory.VehicleDtoFactory.create_VehicleDto_Request;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class CustomerDtoFactory {

    public static Request create_CustomerDto_Request() {
        return new Request(CustomerDto.Request.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final CustomerDto.Request.RequestBuilder builder;

        public CustomerDto.Request withAllFields() {
            var result = builder
                    // Self
                    .username("john.doe@example.com")
                    .name("John Doe")
                    .password("1234asdl")
                    .email("john.doe@example.com")
                    .document("27.614.623/0001-00")
                    // Composition
                    .vehicle(create_VehicleDto_Request().withAllFields())
                    .build();

            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public CustomerDto.Request valid() {
            return builder
                    .username("john.doe@example.com")
                    .name("John Doe")
                    .password("1234asdl")
                    .email("john.doe@example.com")
                    .document("27.614.623/0001-00")
                    .build();
        }

        public CustomerDto.Request initiatedEmpty() {
            return builder.build();
        }
    }
}