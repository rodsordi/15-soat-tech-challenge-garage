package br.com.fiap.garage.application.v1.dto.factory;

import br.com.fiap.garage.application.v1.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmployeeDtoFactory {

    public static Request create_EmployeeDto_Request() {
        return new Request(EmployeeDto.Request.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final EmployeeDto.Request.RequestBuilder builder;

        public EmployeeDto.Request withAllFields() {
            var result = builder
                    .username("john.doe@garage.com")
                    .password("1a2s3d4f")
                    .name("John")
                    .email("john.doe@garage.com")
                    .cpf("690.059.750-59")
                    .build();

            assertThatObject(result).hasNoEmptyFields();
            return result;
        }

        public EmployeeDto.Request valid() {
            return builder
                    .username("jack.doe@garage.com")
                    .password("1a2s3d4f")
                    .name("Jack")
                    .email("jack.doe@garage.com")
                    .cpf("939.043.210-30")
                    .build();
        }

        public EmployeeDto.Request initiatedEmpty() {
            return builder.build();
        }
    }
}