package br.com.fiap.garage.application.v1.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.garage.application.v1.dto.assertions.VehicleDtoAssertions.assertThat_VehicleDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.VehicleDtoAssertions.assertThat_VehicleDto_Response;
import static br.com.fiap.garage.application.v1.dto.factory.VehicleDtoFactory.create_VehicleDto_Request;
import static br.com.fiap.garage.domain.entity.assertions.VehicleAssertions.assertThat_Vehicle;
import static br.com.fiap.garage.domain.entity.factory.VehicleFactory.create_Vehicle;
import static org.mapstruct.factory.Mappers.getMapper;

class VehicleDtoMapperTest {

    private static final VehicleDtoMapper MAPPER = getMapper(VehicleDtoMapper.class);

    @DisplayName("When converting VehicleDto.Request to Vehicle")
    @Nested
    class Convert1 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a VehicleDto.Request with all fields")
            @Test
            void test1() {
                //Given
                var source = create_VehicleDto_Request()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_Vehicle(actual)
                        .wasConvertedFrom_VehicleDto_Request();
            }
        }
    }

    @DisplayName("When converting Vehicle to VehicleDto.Response")
    @Nested
    class Convert2 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Vehicle with all fields")
            @Test
            void test1() {
                //Given
                var source = create_Vehicle()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_VehicleDto_Response(actual)
                        .wasConvertedFrom_Vehicle();
            }
        }
    }

    @DisplayName("When converting Vehicle to VehicleDto.Representation")
    @Nested
    class ConvertToRepresentation {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Vehicle with all fields")
            @Test
            void test1() {
                //Given
                var source = create_Vehicle()
                        .withAllFields();
                //When
                var actual = MAPPER.convertToRepresentation(source);
                //Then
                assertThat_VehicleDto_Representation(actual)
                        .wasConvertedFrom_Vehicle();
            }
        }
    }
}