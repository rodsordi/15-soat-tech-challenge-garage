package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.InventoryMaterialDto;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.assertions.MaterialDtoAssertions.assertThat_MaterialDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.MaterialDtoAssertions.assertThat_MaterialDto_Request;
import static br.com.fiap.garage.application.v1.dto.assertions.MaterialDtoAssertions.assertThat_MaterialDto_Response;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class InventoryMaterialDtoAssertions {

    // --- REQUEST ---

    public static Request assertThat_InventoryMaterialDto_Request(InventoryMaterialDto.Request actual) {
        assertThat(actual).isNotNull();
        return new Request(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final InventoryMaterialDto.Request actual;

        /**
         * @see br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory.Request
         * .withAllFields()
         */
        public void isEqualTo_Request() {
            // Self
            assertThat(actual.getQuantityInStock())
                    .isEqualTo(100);

            // Composition
            assertThat_MaterialDto_Request(actual.getMaterial())
                    .isEqualTo_Request();

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }

    // --- RESPONSE ---

    public static Response assertThat_InventoryMaterialDto_Response(InventoryMaterialDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final InventoryMaterialDto.Response actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_InventoryMaterial() {
            // Self
            assertThat(actual.getId())
                    .hasToString("9d1b9b7c-bd7c-4f5f-a747-0b1f63aac409");
            assertThat(actual.getQuantityInStock())
                    .isEqualTo(100);
            assertThat(actual.getReservedQuantity())
                    .isEqualTo(15);

            // Composition
            assertThat_MaterialDto_Response(actual.getMaterial())
                    .wasConvertedFrom_Material();

            // Inheritance (AuditableEntity)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));
            assertThat(actual.getUpdatedAt())
                    .isEqualTo(newDateTime("14/12/2026 23:59:59"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }

    // --- REPRESENTATION ---

    public static Representation assertThat_InventoryMaterialDto_Representation(InventoryMaterialDto.Representation actual) {
        assertThat(actual).isNotNull();
        return new Representation(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final InventoryMaterialDto.Representation actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_InventoryMaterial() {
            // Self
            assertThat(actual.getId())
                    .hasToString("9d1b9b7c-bd7c-4f5f-a747-0b1f63aac409");
            assertThat(actual.getQuantityInStock())
                    .isEqualTo(100);
            assertThat(actual.getReservedQuantity())
                    .isEqualTo(15);

            // Composition
            assertThat_MaterialDto_Representation(actual.getMaterial())
                    .wasConvertedFrom_Material();

            // Inheritance (AuditableEntity)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));

            // RepresentationModel
            assertThat(actual.getLinks())
                    .hasToString("</v1/inventory-materials/9d1b9b7c-bd7c-4f5f-a747-0b1f63aac409>;rel=\"self\"");

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}