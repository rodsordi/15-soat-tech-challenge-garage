package br.com.fiap.garage.application.v1.mapper;

import br.com.fiap.garage.application.v1.dto.NotificationDto;
import br.com.fiap.garage.domain.entity.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.assertions.NotificationDtoAssertions.assertThat_NotificationDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.NotificationDtoAssertions.assertThat_NotificationDto_Response;
import static br.com.fiap.garage.application.v1.dto.factory.NotificationDtoFactory.create_NotificationDto_Request;
import static br.com.fiap.garage.domain.entity.assertions.NotificationAssertions.assertThat_Notification;
import static br.com.fiap.garage.domain.entity.factory.NotificationFactory.create_Notification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class NotificationDtoMapperTest {

    private static final NotificationDtoMapper mapper = getMapper(NotificationDtoMapper.class);

    @DisplayName("When convert NotificationDto.Request to Notification")
    @Nested
    class Convert1 {

        @DisplayName("Then must execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a NotificationDto.Request with all fields")
            @Test
            void test1() {
                //Given
                var source = create_NotificationDto_Request()
                        .withAllFields();
                //When
                var actual = mapper.convert(source);
                //Then
                assertThat_Notification(actual)
                        .wasConvertedFrom_NotificationDto_Request();
            }

            @DisplayName("Given a null NotificationDto.Request")
            @Test
            void test2() {
                //Given
                NotificationDto.Request source = null;
                //When
                var actual = mapper.convert(source);
                //Then
                assertThat(actual)
                        .isNull();
            }

            @DisplayName("Given a NotificationDto.Request with all fields, but with null composition")
            @Test
            void test3() {
                //Given
                var source = create_NotificationDto_Request()
                        .withAllFields();
                setField(source, "email", null);
                //When
                var actual = mapper.convert(source);
                //Then
                assertThat(actual.getEmail())
                        .isNull();
            }
        }
    }

    @DisplayName("When convert Notification to NotificationDto.Response")
    @Nested
    class Convert2 {

        @DisplayName("Then must execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a Notification with all fields")
            @Test
            void test1() {
                //Given
                var notification = create_Notification().withAllFields();
                //When
                var actual = mapper.convert(notification);
                //Then
                assertThat_NotificationDto_Response(actual)
                        .wasConvertedFrom_Notification();
            }

            @DisplayName("Given a null Notification")
            @Test
            void test2() {
                //Given
                Notification notification = null;
                //When
                var actual = mapper.convert(notification);
                //Then
                assertThat(actual)
                        .isNull();
            }

            @DisplayName("Given an empty Notification")
            @Test
            void test3() {
                //Given
                var notification = Notification.builder().build();
                //When
                var actual = mapper.convert(notification);
                //Then
                assertThatObject(actual)
                        .hasAllFieldsEmpty();
            }

            @DisplayName("Given a Notification with all fields and null composition")
            @Test
            void test5() {
                //Given
                var notification = create_Notification().withAllFields();
                setField(notification, "email", null);
                //When
                var actual = mapper.convert(notification);
                //Then
                assertThat(actual.getEmail())
                        .isNull();
            }
        }
    }

    @DisplayName("When convert Notification to NotificationDto.Representation")
    @Nested
    class ConvertToRepresentation {

        @DisplayName("Then must execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a null Notification")
            @Test
            void test1() {
                //Given
                Notification notification = null;
                //When
                var actual = mapper.convertToRepresentation(notification);
                //Then
                assertThat(actual)
                        .isNull();
            }

            @DisplayName("Given an empty Notification")
            @Test
            void test2() {
                //Given
                var notification = Notification.builder().build();
                //When
                var actual = mapper.convertToRepresentation(notification);
                //Then
                assertThatObject(actual)
                        .hasAllFieldsEmpty();
            }

            @DisplayName("Given a Notification with all fields")
            @Test
            void test4() {
                //Given
                var notification = create_Notification().withAllFields();
                //When
                var actual = mapper.convertToRepresentation(notification);
                //Then
                assertThat_NotificationDto_Representation(actual)
                        .wasConvertedFrom_Notification();
            }
        }
    }
}