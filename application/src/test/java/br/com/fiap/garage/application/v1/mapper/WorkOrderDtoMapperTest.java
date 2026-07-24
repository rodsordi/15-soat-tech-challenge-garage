package br.com.fiap.garage.application.v1.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.garage.application.v1.dto.assertions.WorkOrderDtoAssertions.assertThat_WorkOrderDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.WorkOrderDtoAssertions.assertThat_WorkOrderDto_Response;
import static br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory.create_WorkOrderDto_Request;
import static br.com.fiap.garage.domain.entity.assertions.WorkOrderAssertions.assertThat_WorkOrder;
import static br.com.fiap.garage.domain.entity.factory.WorkOrderFactory.create_WorkOrder;
import static org.mapstruct.factory.Mappers.getMapper;

class WorkOrderDtoMapperTest {

    private static final WorkOrderDtoMapper MAPPER = getMapper(WorkOrderDtoMapper.class);

    @DisplayName("When converting WorkOrderDto.Request to WorkOrder")
    @Nested
    class Convert1 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a WorkOrderDto.Request with all fields")
            @Test
            void test1() {
                //Given
                var source = create_WorkOrderDto_Request()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_WorkOrder(actual)
                        .wasConvertedFrom_WorkOrderDto_Request();
            }
        }
    }

    @DisplayName("When converting WorkOrder to WorkOrderDto.Response")
    @Nested
    class Convert2 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a WorkOrder with all fields")
            @Test
            void test1() {
                //Given
                var source = create_WorkOrder()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_WorkOrderDto_Response(actual)
                        .wasConvertedFrom_WorkOrder();
            }
        }
    }

    @DisplayName("When converting WorkOrder to WorkOrderDto.Representation")
    @Nested
    class ConvertToRepresentation {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a WorkOrder with all fields")
            @Test
            void test1() {
                //Given
                var source = create_WorkOrder()
                        .withAllFields();
                //When
                var actual = MAPPER.convertToRepresentation(source);
                //Then
                assertThat_WorkOrderDto_Representation(actual)
                        .wasConvertedFrom_WorkOrder();
            }
        }
    }
}