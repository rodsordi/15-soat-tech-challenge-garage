package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.repository.EmployeeRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeUpdateUseCaseTest {

    @InjectMocks
    private EmployeeUpdateUseCase employeeUpdateUseCase;

    @Mock
    private EmployeeRepository repository;

    @DisplayName("When updating Employee")
    @Nested
    class Update {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_Employee()
                                .withAllFields()));
                when(repository.save(any()))
                        .thenAnswer(i -> i.getArgument(0));
            }

            @DisplayName("Given a valid id, and a Employee with all fields")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var employee = create_Employee()
                        .withAllFields();
                //When
                var actual = employeeUpdateUseCase.update(id, employee);
                //Then
                assertThat(actual).isNotNull();
                verify(repository, times(1)).save(any());
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a Employee with all fields")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var employee = create_Employee()
                        .withAllFields();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> employeeUpdateUseCase.update(id, employee));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Employee] with [id]: [b06b216f-215d-41eb-8c03-570f03562064] not found");
            }
        }
    }
}