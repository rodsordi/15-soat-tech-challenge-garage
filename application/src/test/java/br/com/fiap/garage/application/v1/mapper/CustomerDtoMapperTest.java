package br.com.fiap.garage.application.v1.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.garage.application.v1.dto.assertions.CustomerDtoAssertions.assertThat_CustomerDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.CustomerDtoAssertions.assertThat_CustomerDto_Response;
import static br.com.fiap.garage.application.v1.dto.factory.CustomerDtoFactory.create_CustomerDto_Request;
import static br.com.fiap.garage.domain.entity.assertions.CustomerAssertions.assertThat_Customer;
import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
import static org.mapstruct.factory.Mappers.getMapper;

class CustomerDtoMapperTest {

    private static final CustomerDtoMapper MAPPER = getMapper(CustomerDtoMapper.class);

    @DisplayName("When converting CustomerDto.Request to Customer")
    @Nested
    class Convert1 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a CustomerDto.Request with all fields")
            @Test
            void test1() {
                //Given
                var source = create_CustomerDto_Request()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_Customer(actual)
                        .wasConvertedFrom_CustomerDto_Request();
            }
        }
    }

    @DisplayName("When converting Customer to CustomerDto.Response")
    @Nested
    class Convert2 {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Customer with all fields")
            @Test
            void test1() {
                //Given
                var source = create_Customer()
                        .withAllFields();
                //When
                var actual = MAPPER.convert(source);
                //Then
                assertThat_CustomerDto_Response(actual)
                        .wasConvertedFrom_Customer();
            }
        }
    }

    @DisplayName("When converting Customer to CustomerDto.Representation")
    @Nested
    class ConvertToRepresentation {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Customer with all fields")
            @Test
            void test1() {
                //Given
                var source = create_Customer()
                        .withAllFields();
                //When
                var actual = MAPPER.convertToRepresentation(source);
                //Then
                assertThat_CustomerDto_Representation(actual)
                        .wasConvertedFrom_Customer();
            }
        }
    }
}