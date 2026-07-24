package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.filter.ServiceFilter;
import br.com.fiap.garage.domain.repository.ServiceRepository;
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

import static br.com.fiap.garage.domain.entity.factory.ServiceFactory.create_Service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceSearchUseCaseTest {

    @InjectMocks
    private ServiceSearchUseCase serviceSearchUseCase;

    @Mock
    private ServiceRepository repository;

    @DisplayName("When finding Service by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_Service()
                                .withAllFields()));
            }

            @DisplayName("Given a valid service id")
            @Test
            void test1() {
                //Given
                var serviceId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = serviceSearchUseCase.findById(serviceId);
                //Then
                assertThat(actual)
                        .isNotNull();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a serviceId, in scenario with no registered Service")
            @Test
            void test1() {
                //Given
                var serviceId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> serviceSearchUseCase.findById(serviceId));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Service] with [id]: [fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2] not found");
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
                                create_Service().withAllFields(),
                                create_Service().withAllFields()
                        )));
            }

            @DisplayName("Given a valid inventory filter")
            @Test
            void test1() {
                //Given
                var inventoryFilter = new ServiceFilter();
                //When
                var actual = serviceSearchUseCase.findAll(inventoryFilter);
                //Then
                assertThat(actual)
                        .isNotEmpty();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given an empty Service filter, in scenario with no registered Service")
            @Test
            void test1() {
                //Given
                var serviceFilter = new ServiceFilter();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> serviceSearchUseCase.findAll(serviceFilter));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Service] not found");
            }
        }
    }
}