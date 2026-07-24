package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.garage.application.v1.dto.ServiceDto;
import br.com.fiap.garage.domain.entity.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.assertions.ServiceDtoAssertions.assertThat_ServiceDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.ServiceDtoAssertions.assertThat_ServiceDto_Response;
import static br.com.fiap.garage.application.v1.dto.factory.ServiceDtoFactory.create_ServiceDto_Request;
import static br.com.fiap.garage.domain.entity.assertions.ServiceAssertions.assertThat_Service;
import static br.com.fiap.garage.domain.entity.factory.ServiceFactory.create_Service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class ServiceDtoMapperTest {

    private static final ServiceDtoMapper mapper = getMapper(ServiceDtoMapper.class);

    @DisplayName("When convert ServiceDto.Request to Service")
    @Nested
    class Convert1 {

        @DisplayName("Then must execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a ServiceDto.Request with all fields")
            @Test
            void test1() {
                //Given
                var source = create_ServiceDto_Request()
                        .withAllFields();
                //When
                var actual = mapper.convert(source);
                //Then
                assertThat_Service(actual)
                        .wasConvertedFrom_ServiceDto();
            }

            @DisplayName("Given a null ServiceDto.Request")
            @Test
            void test2() {
                //Given
                ServiceDto.Request source = null;
                //When
                var actual = mapper.convert(source);
                //Then
                assertThat(actual)
                        .isNull();
            }

            @DisplayName("Given a ServiceDto.Request with all fields, but with null composition")
            @Test
            void test3() {
                //Given
                var source = create_ServiceDto_Request()
                        .withAllFields();
                setField(source, "materialsIds", null);
                //When
                var actual = mapper.convert(source);
                //Then
                assertThat(actual.getMaterials())
                        .isNullOrEmpty();
            }
        }
    }

    @DisplayName("When convert Service to ServiceDto.Response")
    @Nested
    class Convert2 {

        @DisplayName("Then must execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Service with all fields")
            @Test
            void test1() {
                //Given
                var service = create_Service().withAllFields();
                //When
                var actual = mapper.convert(service);
                //Then
                assertThat_ServiceDto_Response(actual)
                        .wasConvertedFrom_Service();
            }

            @DisplayName("Given a null Service")
            @Test
            void test2() {
                //Given
                Service service = null;
                //When
                var actual = mapper.convert(service);
                //Then
                assertThat(actual)
                        .isNull();
            }

            @DisplayName("Given an empty Service")
            @Test
            void test3() {
                //Given
                var service = Service.builder()
                        .materials(null)
                        .build();
                //When
                var actual = mapper.convert(service);
                //Then
                assertThatObject(actual)
                        .hasAllFieldsEmpty();
            }

            @DisplayName("Given an initiated empty Service")
            @Test
            void test4() {
                //Given
                var service = create_Service().initiatedEmpty();
                //When
                var actual = mapper.convert(service);
                //Then
                assertThatObject(actual)
                        .hasAllFieldsEmpty();
            }

            @DisplayName("Given a Service with all fields and null composition")
            @Test
            void test5() {
                //Given
                var service = create_Service().withAllFields();
                setField(service, "materials", null);
                //When
                var actual = mapper.convert(service);
                //Then
                assertThat(actual.getMaterials())
                        .isNullOrEmpty();
            }
        }
    }

    @DisplayName("When convert Service to ServiceDto.Representation")
    @Nested
    class ConvertToRepresentation {

        @DisplayName("Then must execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a null Service")
            @Test
            void test1() {
                //Given
                Service service = null;
                //When
                var actual = mapper.convertToRepresentation(service);
                //Then
                assertThat(actual)
                        .isNull();
            }

            @DisplayName("Given an empty Service")
            @Test
            void test2() {
                //Given
                var service = Service.builder()
                        .material(null)
                        .build();
                //When
                var actual = mapper.convertToRepresentation(service);
                //Then
                assertThatObject(actual)
                        .hasAllFieldsEmpty();
            }

            @DisplayName("Given an initiated empty Service")
            @Test
            void test3() {
                //Given
                var service = create_Service().initiatedEmpty();
                //When
                var actual = mapper.convertToRepresentation(service);
                //Then
                assertThatObject(actual)
                        .hasAllFieldsEmpty();
            }

            @DisplayName("Given a Service with all fields")
            @Test
            void test4() {
                //Given
                var service = create_Service().withAllFields();
                //When
                var actual = mapper.convertToRepresentation(service);
                //Then
                assertThat_ServiceDto_Representation(actual)
                        .wasConvertedFrom_Service();
            }
        }
    }
}