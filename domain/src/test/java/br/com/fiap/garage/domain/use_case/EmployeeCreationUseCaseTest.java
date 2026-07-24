package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.Employee;
import br.com.fiap.garage.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static br.com.fiap.garage.domain.entity.factory.EmployeeFactory.create_Employee;
import static java.util.UUID.fromString;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class EmployeeCreationUseCaseTest {

    @InjectMocks
    private EmployeeCreationUseCase employeeCreationUseCase;

    @Mock
    private EmployeeRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("When creating Employee")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.save(any()))
                        .thenAnswer(invocationOnMock -> {
                            Employee employee = invocationOnMock.getArgument(0);
                            setField(employee, "id", fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577"));
                            return employee;
                        });
                when(passwordEncoder.encode(any()))
                        .thenAnswer(invocation -> invocation.getArgument(0));
            }

            @DisplayName("Given a Employee with all fields")
            @Test
            void test1() {
                //Given
                var employee = create_Employee()
                        .withAllFields();
                //When
                var actual = employeeCreationUseCase.create(employee);
                //Then
                assertThat(actual.getId())
                        .hasToString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577");
            }
        }
    }
}