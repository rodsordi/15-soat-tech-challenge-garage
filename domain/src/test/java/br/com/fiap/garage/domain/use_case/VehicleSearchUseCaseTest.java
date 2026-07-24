package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.filter.VehicleFilter;
import br.com.fiap.garage.domain.repository.VehicleRepository;
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

import static br.com.fiap.garage.domain.entity.factory.VehicleFactory.create_Vehicle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleSearchUseCaseTest {

    @InjectMocks
    private VehicleSearchUseCase vehicleSearchUseCase;

    @Mock
    private VehicleRepository repository;

    @DisplayName("When finding Vehicle by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_Vehicle()
                                .withAllFields()));
            }

            @DisplayName("Given a valid vehicle id")
            @Test
            void test1() {
                //Given
                var vehicleId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = vehicleSearchUseCase.findById(vehicleId);
                //Then
                assertThat(actual)
                        .isNotNull();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a vehicleId, in scenario with no registered Vehicle")
            @Test
            void test1() {
                //Given
                var vehicleId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> vehicleSearchUseCase.findById(vehicleId));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Vehicle] with [id]: [fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2] not found");
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
                                create_Vehicle().withAllFields(),
                                create_Vehicle().withAllFields()
                        )));
            }

            @DisplayName("Given a valid inventory filter")
            @Test
            void test1() {
                //Given
                var inventoryFilter = new VehicleFilter();
                //When
                var actual = vehicleSearchUseCase.findAll(inventoryFilter);
                //Then
                assertThat(actual)
                        .isNotEmpty();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given an empty Vehicle filter, in scenario with no registered Vehicle")
            @Test
            void test1() {
                //Given
                var vehicleFilter = new VehicleFilter();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> vehicleSearchUseCase.findAll(vehicleFilter));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Vehicle] not found");
            }
        }
    }
}