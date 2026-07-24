package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.MaterialDto;
import br.com.fiap.garage.domain.enums.MaterialType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.enums.MaterialType.SHOP_SUPPLY;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class MaterialDtoAssertions {

    // --- REQUEST ---

    public static Request assertThat_MaterialDto_Request(MaterialDto.Request actual) {
        assertThat(actual).isNotNull();
        return new Request(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final MaterialDto.Request actual;

        /**
         * @see br.com.fiap.garage.application.v1.dto.factory.MaterialDtoFactory.Request
         * .withAllFields()
         */
        public void isEqualTo_Request() {
            // Self
            assertThat(actual.getType())
                    .isEqualTo(MaterialType.values()[0]);
            assertThat(actual.getName())
                    .isEqualTo("Synthetic Engine Oil");
            assertThat(actual.getDescription())
                    .isEqualTo("High-performance 5W-30 synthetic oil");
            assertThat(actual.getCost())
                    .isEqualTo(new BigDecimal("45.50"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }

    // --- RESPONSE ---

    public static Response assertThat_MaterialDto_Response(MaterialDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {

        private final MaterialDto.Response actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.MaterialFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Material() {
            // Self
            assertThat(actual.getId())
                    .hasToString("73ffaed5-ebc3-4c28-901d-b0240c30b639");
            assertThat(actual.getType())
                    .isEqualTo(SHOP_SUPPLY);
            assertThat(actual.getName())
                    .isEqualTo("Engine Oil");
            assertThat(actual.getDescription())
                    .isEqualTo("Synthetic 5W-30 motor oil");
            assertThat(actual.getCost())
                    .isEqualByComparingTo(new BigDecimal("150.00"));

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

    public static Representation assertThat_MaterialDto_Representation(MaterialDto.Representation actual) {
        assertThat(actual).isNotNull();
        return new Representation(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final MaterialDto.Representation actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.MaterialFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_Material() {
            // Self
            assertThat(actual.getId())
                    .hasToString("73ffaed5-ebc3-4c28-901d-b0240c30b639");
            assertThat(actual.getType())
                    .isEqualTo(SHOP_SUPPLY);
            assertThat(actual.getName())
                    .isEqualTo("Engine Oil");
            assertThat(actual.getCost())
                    .isEqualByComparingTo(new BigDecimal("150.00"));

            // Inheritance (AuditableEntity)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}