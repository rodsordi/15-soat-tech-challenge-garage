package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.filter.NotificationFilter;
import br.com.fiap.garage.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.garage.domain.entity.factory.NotificationFactory.create_Notification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationSearchUseCaseTest {

    @InjectMocks
    private NotificationSearchUseCase notificationSearchUseCase;

    @Mock
    private NotificationRepository repository;

    @DisplayName("When finding Notification by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_Notification()
                                .withAllFields()));
            }

            @DisplayName("Given a valid notification id")
            @Test
            void test1() {
                //Given
                var notificationId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = notificationSearchUseCase.findById(notificationId);
                //Then
                assertThat(actual)
                        .isNotNull();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a notificationId, in scenario with no registered Notification")
            @Test
            void test1() {
                //Given
                var notificationId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> notificationSearchUseCase.findById(notificationId));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Notification] with [id]: [fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2] not found");
            }
        }
    }
    
    @DisplayName("When finding all Inventories")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                lenient()
                        .when(repository.findAll(any(), any()))
                        .thenReturn(new PageImpl<>(List.of(
                                create_Notification().withAllFields(),
                                create_Notification().withAllFields()
                        )));
            }

            @DisplayName("Given a valid inventory filter")
            @Test
            void test1() {
                //Given
                var inventoryFilter = new NotificationFilter();
                //When
                var actual = notificationSearchUseCase.findAll(inventoryFilter);
                //Then
                assertThat(actual)
                        .isNotEmpty();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given an empty Notification filter, in scenario with no registered Notification")
            @Test
            void test1() {
                //Given
                var notificationFilter = new NotificationFilter();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> notificationSearchUseCase.findAll(notificationFilter));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Notification] not found");
            }
        }
    }
}