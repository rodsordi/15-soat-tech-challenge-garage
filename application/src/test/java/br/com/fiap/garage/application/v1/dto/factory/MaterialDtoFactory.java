package br.com.fiap.garage.application.v1.dto.factory;

import br.com.fiap.garage.application.v1.dto.MaterialDto;
import br.com.fiap.garage.domain.enums.MaterialType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.enums.MaterialType.SHOP_SUPPLY;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class MaterialDtoFactory {

    public static Request create_MaterialDto_Request() {
        return new Request(MaterialDto.Request.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final MaterialDto.Request.RequestBuilder builder;

        public MaterialDto.Request withAllFields() {
            var result = builder
                    .type(SHOP_SUPPLY)
                    .name("Synthetic Engine Oil")
                    .description("Oil 5W-30")
                    .cost(new BigDecimal("85.50"))
                    .build();
            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public MaterialDto.Request valid() {
            return builder
                    .type(SHOP_SUPPLY)
                    .name("Synthetic Engine Oil")
                    .description("Oil 5W-30")
                    .cost(new BigDecimal("85.50"))
                    .build();
        }

        public MaterialDto.Request initiatedEmpty() {
            return builder.build();
        }
    }

    public static Response create_MaterialDto_Response() {
        return new Response(MaterialDto.Response.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final MaterialDto.Response.ResponseBuilder builder;

        public MaterialDto.Response withAllFields() {
            var result = builder
                    .id(fromString("4f5f9b7c-bd7c-4f5f-a747-0b1f63aac409"))
                    .type(MaterialType.values()[0])
                    .name("Synthetic Engine Oil")
                    .description("High-performance 5W-30 synthetic oil")
                    .cost(new BigDecimal("45.50"))
                    .createdAt(newDateTime("30/12/2024 23:59:59"))
                    .updatedAt(newDateTime("31/12/2024 23:59:59"))
                    .build();
            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public MaterialDto.Response valid() {
            return builder
                    .id(fromString("123e4567-e89b-12d3-a456-426614174000"))
                    .name("Standard Brake Pad")
                    .cost(new BigDecimal("120.00"))
                    .build();
        }

        public MaterialDto.Response initiatedEmpty() {
            return builder.build();
        }
    }

    public static Representation create_MaterialDto_Representation() {
        return new Representation(MaterialDto.Representation.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final MaterialDto.Representation.RepresentationBuilder builder;

        public MaterialDto.Representation withAllFields() {
            var result = builder
                    .id(fromString("4f5f9b7c-bd7c-4f5f-a747-0b1f63aac409"))
                    .type(MaterialType.values()[0])
                    .name("Synthetic Engine Oil")
                    .cost(new BigDecimal("45.50"))
                    .createdAt(newDateTime("30/12/2024 23:59:59"))
                    .build();
            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public MaterialDto.Representation valid() {
            return builder
                    .id(fromString("123e4567-e89b-12d3-a456-426614174000"))
                    .name("Standard Brake Pad")
                    .cost(new BigDecimal("120.00"))
                    .build();
        }

        public MaterialDto.Representation initiatedEmpty() {
            return builder.build();
        }
    }
}