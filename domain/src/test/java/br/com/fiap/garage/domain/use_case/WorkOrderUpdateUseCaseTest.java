package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.publisher.NotifyCustomerForApprovalPublisher;
import br.com.fiap.garage.domain.repository.EmployeeRepository;
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
import java.util.UUID;

import static br.com.fiap.garage.domain.entity.factory.EmployeeFactory.create_Employee;
import static br.com.fiap.garage.domain.entity.factory.WorkOrderFactory.create_WorkOrder;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.DIAGNOSING;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.EXECUTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderUpdateUseCaseTest {

    @InjectMocks
    private WorkOrderUpdateUseCase workOrderUpdateUseCase;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private NotifyCustomerForApprovalPublisher notifyCustomerForApprovalPublisher;

    @DisplayName("When updating WorkOrder status")
    @Nested
    class UpdateStatus {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(workOrderRepository.findById(any()))
                        .thenReturn(Optional.of(create_WorkOrder()
                                .withAllFields()));
                when(workOrderRepository.save(any()))
                        .thenAnswer(i -> i.getArgument(0));
            }

            @DisplayName("Given a valid id, and a valid status")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var status = DIAGNOSING;
                //When
                var actual = workOrderUpdateUseCase.updateStatus(id, status);
                //Then
                assertThat(actual).isNotNull();
                verify(workOrderRepository, times(1)).save(any());
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a WorkOrder with all fields, in scenario with no registered WorkOrder")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var status = EXECUTING;
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> workOrderUpdateUseCase.updateStatus(id, status));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [WorkOrder] with [id]: [b06b216f-215d-41eb-8c03-570f03562064] not found");
            }
        }
    }

    @DisplayName("When updating WorkOrder employee")
    @Nested
    class UpdateEmployee {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(workOrderRepository.findById(any()))
                        .thenReturn(Optional.of(create_WorkOrder()
                                .withAllFields()));
                when(workOrderRepository.save(any()))
                        .thenAnswer(i -> i.getArgument(0));
                when(employeeRepository.findById(any()))
                        .thenReturn(Optional.of(create_Employee()
                                .withAllFields()));
            }

            @DisplayName("Given a valid id, and a valid employeeId")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var employeeId = UUID.fromString("7e7a3f69-4e5f-4491-91e0-5c78d706c9bd");
                //When
                var actual = workOrderUpdateUseCase.updateEmployee(id, employeeId);
                //Then
                assertThat(actual).isNotNull();
                verify(workOrderRepository, times(1)).save(any());
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a WorkOrder with all fields, in scenario with no registered WorkOrder")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var employeeId = UUID.fromString("7e7a3f69-4e5f-4491-91e0-5c78d706c9bd");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> workOrderUpdateUseCase.updateEmployee(id, employeeId));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [WorkOrder] with [id]: [b06b216f-215d-41eb-8c03-570f03562064] not found");
            }

            @DisplayName("Given a WorkOrder with all fields, in scenario with no registered Employee")
            @Test
            void test2() {
                //Scenario
                when(workOrderRepository.findById(any()))
                        .thenReturn(Optional.of(create_WorkOrder()
                                .withAllFields()));
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var employeeId = UUID.fromString("7e7a3f69-4e5f-4491-91e0-5c78d706c9bd");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> workOrderUpdateUseCase.updateEmployee(id, employeeId));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Employee] with [id]: [7e7a3f69-4e5f-4491-91e0-5c78d706c9bd] not found");
            }
        }
    }

    @DisplayName("When finishing a WorkOrder service")
    @Nested
    class FinishService {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(workOrderRepository.findById(any()))
                        .thenReturn(Optional.of(create_WorkOrder()
                                .withAllFields()));
                when(workOrderRepository.save(any()))
                        .thenAnswer(i -> i.getArgument(0));
            }

            @DisplayName("Given a valid id, and a valid serviceToBeFinished")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var serviceToBeFinished = UUID.fromString("7e7a3f69-4e5f-4491-91e0-5c78d706c9bd");
                //When
                var actual = workOrderUpdateUseCase.finishService(id, serviceToBeFinished);
                //Then
                assertThat(actual).isNotNull();
                verify(workOrderRepository, times(1)).save(any());
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a WorkOrder with all fields, in scenario with no registered WorkOrder")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var serviceToBeFinished = UUID.fromString("7e7a3f69-4e5f-4491-91e0-5c78d706c9bd");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> workOrderUpdateUseCase.finishService(id, serviceToBeFinished));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [WorkOrder] with [id]: [b06b216f-215d-41eb-8c03-570f03562064] not found");
            }
        }
    }
}