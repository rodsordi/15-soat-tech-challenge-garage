package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.filter.WorkOrderFilter;
import br.com.fiap.garage.domain.repository.WorkOrderRepository;
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

import static br.com.fiap.garage.domain.entity.factory.WorkOrderFactory.create_WorkOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderSearchUseCaseTest {

    @InjectMocks
    private WorkOrderSearchUseCase workOrderSearchUseCase;

    @Mock
    private WorkOrderRepository repository;

    @DisplayName("When finding WorkOrder by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_WorkOrder()
                                .withAllFields()));
            }

            @DisplayName("Given a valid workOrder id")
            @Test
            void test1() {
                //Given
                var workOrderId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = workOrderSearchUseCase.findById(workOrderId);
                //Then
                assertThat(actual)
                        .isNotNull();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a workOrderId, in scenario with no registered WorkOrder")
            @Test
            void test1() {
                //Given
                var workOrderId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> workOrderSearchUseCase.findById(workOrderId));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [WorkOrder] with [id]: [fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2] not found");
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
                                create_WorkOrder().withAllFields(),
                                create_WorkOrder().withAllFields()
                        )));
            }

            @DisplayName("Given a valid inventory filter")
            @Test
            void test1() {
                //Given
                var inventoryFilter = new WorkOrderFilter();
                //When
                var actual = workOrderSearchUseCase.findAll(inventoryFilter);
                //Then
                assertThat(actual)
                        .isNotEmpty();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given an empty WorkOrder filter, in scenario with no registered WorkOrder")
            @Test
            void test1() {
                //Given
                var workOrderFilter = new WorkOrderFilter();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> workOrderSearchUseCase.findAll(workOrderFilter));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [WorkOrder] not found");
            }
        }
    }
}