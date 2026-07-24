package br.com.fiap.garage.domain.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.garage.domain.entity.assertions.EstimatedServiceAssertions.assertThat_EstimatedService;
import static br.com.fiap.garage.domain.entity.factory.ServiceFactory.create_Service;
import static org.mapstruct.factory.Mappers.getMapper;

class ServiceMapperTest {

    private static final ServiceMapper MAPPER = getMapper(ServiceMapper.class);

    @DisplayName("When converting Service to EstimatedService")
    @Nested
    class Convert {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Service with all fields")
            @Test
            void test1() {
                // Given
                var source = create_Service().withAllFields();
                // When
                var actual = MAPPER.convert(source);
                // Then
                assertThat_EstimatedService(actual)
                        .wasConvertedFrom_Service();
            }
        }
    }
}