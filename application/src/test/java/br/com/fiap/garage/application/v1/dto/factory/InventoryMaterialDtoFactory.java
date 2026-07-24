package br.com.fiap.garage.application.v1.dto.factory;

import br.com.fiap.garage.application.v1.dto.InventoryMaterialDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.factory.MaterialDtoFactory.create_MaterialDto_Representation;
import static br.com.fiap.garage.application.v1.dto.factory.MaterialDtoFactory.create_MaterialDto_Request;
import static br.com.fiap.garage.application.v1.dto.factory.MaterialDtoFactory.create_MaterialDto_Response;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class InventoryMaterialDtoFactory {

    // --- REQUEST ---
    public static Request create_InventoryMaterialDto_Request() {
        return new Request(InventoryMaterialDto.Request.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final InventoryMaterialDto.Request.RequestBuilder builder;

        public InventoryMaterialDto.Request withAllFields() {
            var result = builder
                    // Self
                    .quantityInStock(100)
                    // Composition
                    .material(create_MaterialDto_Request().withAllFields())
                    .build();

            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public InventoryMaterialDto.Request valid() {
            return builder
                    .quantityInStock(50)
                    .material(create_MaterialDto_Request().valid())
                    .build();
        }

        public InventoryMaterialDto.Request initiatedEmpty() {
            return builder
                    .material(create_MaterialDto_Request().initiatedEmpty())
                    .build();
        }
    }

    // --- RESPONSE ---
    public static Response create_InventoryMaterialDto_Response() {
        return new Response(InventoryMaterialDto.Response.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final InventoryMaterialDto.Response.ResponseBuilder builder;

        public InventoryMaterialDto.Response withAllFields() {
            var result = builder
                    // Self
                    .id(fromString("9d1b9b7c-bd7c-4f5f-a747-0b1f63aac409"))
                    .quantityInStock(100)
                    .reservedQuantity(15)
                    // Composition
                    .material(create_MaterialDto_Response().withAllFields())
                    // Auditable
                    .createdAt(newDateTime("30/12/2024 23:59:59"))
                    .updatedAt(newDateTime("31/12/2024 23:59:59"))
                    .build();

            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public InventoryMaterialDto.Response valid() {
            return builder
                    .id(fromString("a12b9b7c-cd7c-4f5f-a747-0b1f63aac410"))
                    .quantityInStock(50)
                    .reservedQuantity(5)
                    .material(create_MaterialDto_Response().valid())
                    .build();
        }

        public InventoryMaterialDto.Response initiatedEmpty() {
            return builder
                    .material(create_MaterialDto_Response().initiatedEmpty())
                    .build();
        }
    }

    // --- REPRESENTATION ---
    public static Representation create_InventoryMaterialDto_Representation() {
        return new Representation(InventoryMaterialDto.Representation.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final InventoryMaterialDto.Representation.RepresentationBuilder builder;

        public InventoryMaterialDto.Representation withAllFields() {
            var result = builder
                    // Self
                    .id(fromString("9d1b9b7c-bd7c-4f5f-a747-0b1f63aac409"))
                    // Composition
                    .material(create_MaterialDto_Representation().withAllFields())
                    // Auditable
                    .createdAt(newDateTime("30/12/2024 23:59:59"))
                    .build();

            // And
            assertThatObject(result)
                    .hasNoEmptyFields();
            return result;
        }

        public InventoryMaterialDto.Representation valid() {
            return builder
                    .id(fromString("a12b9b7c-cd7c-4f5f-a747-0b1f63aac410"))
                    .material(create_MaterialDto_Representation().valid())
                    .build();
        }

        public InventoryMaterialDto.Representation initiatedEmpty() {
            return builder
                    .material(create_MaterialDto_Representation().initiatedEmpty())
                    .build();
        }
    }
}