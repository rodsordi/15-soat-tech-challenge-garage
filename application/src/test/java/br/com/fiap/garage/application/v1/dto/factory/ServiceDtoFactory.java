package br.com.fiap.garage.application.v1.dto.factory;

import br.com.fiap.garage.application.v1.dto.ServiceDto;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ServiceDtoFactory {

    public static Request create_ServiceDto_Request() {
        return new Request(ServiceDto.Request.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final ServiceDto.Request.RequestBuilder builder;

        public ServiceDto.Request withAllFields() {
            var result = builder
                    // Self
                    .name("Oil Change")
                    .description("Complete engine oil and filter change")
                    .cost(new BigDecimal("150.00"))
                    // Composition
                    .materialId(UUID.fromString("4d36346e-9eec-47e5-b267-69e1c6219b28"))
                    .materialId(UUID.fromString("f19a4875-b483-4486-adf6-fe581f1aa953"))
                    .materialId(UUID.fromString("058c9925-253a-464d-adc4-55e6e6d89647"))
                    .build();

            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public ServiceDto.Request valid() {
            return builder
                    .name("Oil Change")
                    .description("Complete engine oil and filter change")
                    .cost(new BigDecimal("150.00"))
                    .build();
        }

        public ServiceDto.Request initiatedEmpty() {
            return builder.build();
        }
    }
}