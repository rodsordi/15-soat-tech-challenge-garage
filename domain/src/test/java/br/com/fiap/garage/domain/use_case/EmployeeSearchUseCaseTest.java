package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.filter.EmployeeFilter;
import br.com.fiap.garage.domain.repository.EmployeeRepository;
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

import static br.com.fiap.garage.domain.entity.factory.EmployeeFactory.create_Employee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeSearchUseCaseTest {

    @InjectMocks
    private EmployeeSearchUseCase employeeSearchUseCase;

    @Mock
    private EmployeeRepository repository;

    @DisplayName("When finding Employee by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_Employee()
                                .withAllFields()));
            }

            @DisplayName("Given a valid employee id")
            @Test
            void test1() {
                //Given
                var employeeId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = employeeSearchUseCase.findById(employeeId);
                //Then
                assertThat(actual)
                        .isNotNull();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a employeeId, in scenario with no registered Employee")
            @Test
            void test1() {
                //Given
                var employeeId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> employeeSearchUseCase.findById(employeeId));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Employee] with [id]: [fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2] not found");
            }
        }
    }

    @DisplayName("When finding all Employees")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findAll(any(), any()))
                        .thenReturn(new PageImpl<>(List.of(create_Employee()
                                .withAllFields())));
            }

            @DisplayName("Given a valid employee filter")
            @Test
            void test1() {
                //Given
                var employeeFilter = new EmployeeFilter();
                //When
                var actual = employeeSearchUseCase.findAll(employeeFilter);
                //Then
                assertThat(actual)
                        .isNotEmpty();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given an empty Employee filter, in scenario with no registered Employee")
            @Test
            void test1() {
                //Given
                var employeeFilter = new EmployeeFilter();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> employeeSearchUseCase.findAll(employeeFilter));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Employee] not found");
            }
        }
    }
}