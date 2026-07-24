package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.publisher.NotifyCustomerForApprovalPublisher;
import br.com.fiap.garage.domain.repository.EmployeeRepository;
import br.com.fiap.garage.domain.repository.ServiceRepository;
import br.com.fiap.garage.domain.repository.VehicleRepository;
import br.com.fiap.garage.domain.repository.WorkOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static br.com.fiap.garage.domain.entity.factory.EmployeeFactory.create_Employee;
import static br.com.fiap.garage.domain.entity.factory.ServiceFactory.create_Service;
import static br.com.fiap.garage.domain.entity.factory.VehicleFactory.create_Vehicle;
import static br.com.fiap.garage.domain.entity.factory.WorkOrderFactory.create_WorkOrder;
import static java.util.UUID.fromString;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class WorkOrderCreationUseCaseTest {

    @InjectMocks
    private WorkOrderCreationUseCase workOrderCreationUseCase;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private NotifyCustomerForApprovalPublisher notifyCustomerForApprovalPublisher;

    @DisplayName("When creating WorkOrder")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                //Scenario
                when(employeeRepository.findById(any()))
                        .thenReturn(Optional.of(create_Employee().withAllFields()));
                //And
                when(vehicleRepository.findById(any()))
                        .thenReturn(Optional.of(create_Vehicle().withAllFields()));
                //And
                when(serviceRepository.findById(any()))
                        .thenReturn(Optional.of(create_Service().withAllFields()));
                //And
                when(workOrderRepository.save(any()))
                        .thenAnswer(invocationOnMock -> {
                            WorkOrder workOrder = invocationOnMock.getArgument(0);
                            setField(workOrder, "id", fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577"));
                            return workOrder;
                        });
            }

            @DisplayName("Given a WorkOrder with all fields, set of Services Ids")
            @Test
            void test1() {
                //Given
                var workOrder = create_WorkOrder()
                        .withAllFields();
                var services = Set.of(UUID.fromString("0e966702-d642-44f8-b85e-6e9f31fcb680"));
                //When
                var actual = workOrderCreationUseCase.create(workOrder, services);
                //Then
                assertThat(actual.getId())
                        .hasToString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577");
            }
        }
    }
}