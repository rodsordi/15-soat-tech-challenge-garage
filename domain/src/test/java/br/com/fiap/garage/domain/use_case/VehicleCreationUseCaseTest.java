package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.Vehicle;
import br.com.fiap.garage.domain.repository.CustomerRepository;
import br.com.fiap.garage.domain.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
import static br.com.fiap.garage.domain.entity.factory.VehicleFactory.create_Vehicle;
import static java.util.UUID.fromString;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class VehicleCreationUseCaseTest {

    @InjectMocks
    private VehicleCreationUseCase vehicleCreationUseCase;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @DisplayName("When creating Vehicle")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(customerRepository.findById(any()))
                        .thenReturn(Optional.of(create_Customer()
                                .withAllFields()));
                when(vehicleRepository.save(any()))
                        .thenAnswer(invocationOnMock -> {
                            Vehicle vehicle = invocationOnMock.getArgument(0);
                            setField(vehicle, "id", fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577"));
                            return vehicle;
                        });
            }

            @DisplayName("Given a valid customerId, a Vehicle with all fields")
            @Test
            void test1() {
                //Given
                var customerId = UUID.fromString("5895edf6-49ba-478b-a24a-d931d8d8878e");
                var vehicle = create_Vehicle()
                        .withAllFields();
                //When
                var actual = vehicleCreationUseCase.create(customerId, vehicle);
                //Then
                assertThat(actual.getId())
                        .hasToString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577");
            }
        }
    }
}