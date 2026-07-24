package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.EstimatedServiceDto;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.assertions.EstimatedMaterialDtoAssertions.assertThat_EstimatedMaterialDto_Response;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class EstimatedServiceDtoAssertions {

    public static Response assertThat_EstimatedServiceDto_Response(EstimatedServiceDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {
        
        private final EstimatedServiceDto.Response actual;
        
        /**
         * @see br.com.fiap.garage.domain.entity.factory.EstimatedServiceFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_EstimatedService() {
            // Self
            assertThat(actual.getId())
                    .hasToString("b69f475d-40b4-41bc-b0ab-22500db01821");
            assertThat(actual.getName())
                    .isEqualTo("Engine Overhaul");
            assertThat(actual.getDescription())
                    .isEqualTo("Complete disassembly and rebuilding of the engine block.");
            assertThat(actual.getCost())
                    .isEqualTo(new BigDecimal("4500.00"));
            assertThat(actual.getFinishedAt())
                    .isEqualTo(newDateTime("31/12/2025 18:00:00"));

            // Composition
            assertThat(actual.getEstimatedMaterials())
                    .hasSize(1);
            assertThat_EstimatedMaterialDto_Response(actual.getEstimatedMaterials().stream().findFirst().orElseThrow())
                    .wasConvertedFrom_EstimatedMaterial();

            // Inheritance (AuditableTable / AuditableEntity)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));
            assertThat(actual.getUpdatedAt())
                    .isEqualTo(newDateTime("14/12/2026 23:59:59"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}