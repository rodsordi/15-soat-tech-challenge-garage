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

    @DisplayName("When creating Employee")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                org.mockito.Mockito.lenient().when(repository.save(any()))
                        .thenAnswer(invocationOnMock -> {
                            Employee employee = invocationOnMock.getArgument(0);
                            setField(employee, "id", fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577"));
                            return employee;
                        });
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

            @DisplayName("Given an existing employee with same id, should update and return existing")
            @Test
            void testIdempotency() {
                //Given
                var employeeId = fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577");
                var existing = create_Employee().withAllFields();
                setField(existing, "id", employeeId);
                var incoming = create_Employee().withAllFields();
                setField(incoming, "id", employeeId);
                setField(incoming, "name", "Updated Name");

                org.mockito.Mockito.when(repository.findByCpf(incoming.getCpf()))
                        .thenReturn(java.util.Optional.of(existing));

                //When
                var actual = employeeCreationUseCase.create(incoming);

                //Then
                assertThat(actual.getId()).isEqualTo(employeeId);
                org.mockito.Mockito.verify(repository).save(existing);
            }

            @DisplayName("Given an existing employee with different id, should reconcile identity")
            @Test
            void testReconciliation() {
                //Given
                var oldId = fromString("11111111-1111-1111-1111-111111111111");
                var newId = fromString("22222222-2222-2222-2222-222222222222");
                var existing = create_Employee().withAllFields();
                setField(existing, "id", oldId);
                var incoming = create_Employee().withAllFields();
                setField(incoming, "id", newId);

                org.mockito.Mockito.when(repository.findByCpf(incoming.getCpf()))
                        .thenReturn(java.util.Optional.of(existing));
                org.mockito.Mockito.when(repository.findById(newId))
                        .thenReturn(java.util.Optional.of(incoming));

                //When
                var actual = employeeCreationUseCase.create(incoming);

                //Then
                assertThat(actual.getId()).isEqualTo(newId);
                org.mockito.Mockito.verify(repository).updateIdentity(oldId, newId, incoming.getName(), incoming.getEmail());
            }
        }
    }
}