package br.com.fiap.garage.application.v1.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.garage.application.v1.dto.assertions.EmployeeDtoAssertions.assertThat_EmployeeDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.EmployeeDtoAssertions.assertThat_EmployeeDto_Response;
import static br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory.create_EmployeeDto_Request;
import static br.com.fiap.garage.domain.entity.assertions.EmployeeAssertions.assertThat_Employee;
import static br.com.fiap.garage.domain.entity.factory.EmployeeFactory.create_Employee;
import static org.mapstruct.factory.Mappers.getMapper;

class EmployeeDtoMapperTest {

    private static final EmployeeDtoMapper MAPPER = getMapper(EmployeeDtoMapper.class);

    @DisplayName("When converting EmployeeDto.Request to Employee")
    @Nested
    class Convert1 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a EmployeeDto.Request with all fields")
            @Test
            void test1() {
                //Given
                var source = create_EmployeeDto_Request()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_Employee(actual)
                        .wasConvertedFrom_EmployeeDto_Request();
            }
        }
    }

    @DisplayName("When converting Employee to EmployeeDto.Response")
    @Nested
    class Convert2 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Employee with all fields")
            @Test
            void test1() {
                //Given
                var source = create_Employee()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_EmployeeDto_Response(actual)
                        .wasConvertedFrom_Employee();
            }
        }
    }

    @DisplayName("When converting Employee to EmployeeDto.Representation")
    @Nested
    class ConvertToRepresentation {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Employee with all fields")
            @Test
            void test1() {
                //Given
                var source = create_Employee()
                        .withAllFields();
                //When
                var actual = MAPPER.convertToRepresentation(source);
                //Then
                assertThat_EmployeeDto_Representation(actual)
                        .wasConvertedFrom_Employee();
            }
        }
    }
}