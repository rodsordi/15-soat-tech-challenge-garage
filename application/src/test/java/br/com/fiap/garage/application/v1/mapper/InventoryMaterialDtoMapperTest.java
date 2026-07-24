package br.com.fiap.garage.application.v1.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.garage.application.v1.dto.assertions.InventoryMaterialDtoAssertions.assertThat_InventoryMaterialDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.InventoryMaterialDtoAssertions.assertThat_InventoryMaterialDto_Response;
import static br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory.create_InventoryMaterialDto_Request;
import static br.com.fiap.garage.domain.entity.assertions.InventoryMaterialAssertions.assertThat_InventoryMaterial;
import static br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory.create_InventoryMaterial;
import static org.mapstruct.factory.Mappers.getMapper;

class InventoryMaterialDtoMapperTest {

    private static final InventoryMaterialDtoMapper MAPPER = getMapper(InventoryMaterialDtoMapper.class);

    @DisplayName("When converting InventoryMaterialDto.Request to InventoryMaterial")
    @Nested
    class Convert1 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a InventoryMaterialDto.Request with all fields")
            @Test
            void test1() {
                //Given
                var source = create_InventoryMaterialDto_Request()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_InventoryMaterial(actual)
                        .wasConvertedFrom_InventoryMaterialDto_Request();
            }
        }
    }

    @DisplayName("When converting InventoryMaterial to InventoryMaterialDto.Response")
    @Nested
    class Convert2 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a InventoryMaterial with all fields")
            @Test
            void test1() {
                //Given
                var source = create_InventoryMaterial()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_InventoryMaterialDto_Response(actual)
                        .wasConvertedFrom_InventoryMaterial();
            }
        }
    }

    @DisplayName("When converting InventoryMaterial to InventoryMaterialDto.Representation")
    @Nested
    class ConvertToRepresentation {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a InventoryMaterial with all fields")
            @Test
            void test1() {
                //Given
                var source = create_InventoryMaterial()
                        .withAllFields();
                //When
                var actual = MAPPER.convertToRepresentation(source);
                //Then
                assertThat_InventoryMaterialDto_Representation(actual)
                        .wasConvertedFrom_InventoryMaterial();
            }
        }
    }
}