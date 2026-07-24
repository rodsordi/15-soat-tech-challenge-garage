package br.com.fiap.garage.application.v1.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.garage.application.v1.dto.assertions.MaterialDtoAssertions.assertThat_MaterialDto_Response;
import static br.com.fiap.garage.application.v1.dto.factory.MaterialDtoFactory.create_MaterialDto_Request;
import static br.com.fiap.garage.domain.entity.assertions.MaterialAssertions.assertThat_Material;
import static br.com.fiap.garage.domain.entity.factory.MaterialFactory.create_Material;
import static org.mapstruct.factory.Mappers.getMapper;

class MaterialDtoMapperTest {

    private static final MaterialDtoMapper MAPPER = getMapper(MaterialDtoMapper.class);

    @DisplayName("When converting MaterialDto.Request to Material")
    @Nested
    class Convert1 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a MaterialDto.Request with all fields")
            @Test
            void test1() {
                //Given
                var source = create_MaterialDto_Request()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_Material(actual)
                        .wasConvertedFrom_MaterialDto_Request();
            }
        }
    }

    @DisplayName("When converting Material to MaterialDto.Response")
    @Nested
    class Convert2 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Material with all fields")
            @Test
            void test1() {
                //Given
                var source = create_Material()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_MaterialDto_Response(actual)
                        .wasConvertedFrom_Material();
            }
        }
    }
}