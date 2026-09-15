package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.Customer;
import br.com.fiap.garage.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
import static java.util.UUID.fromString;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class CustomerCreationUseCaseTest {

    @InjectMocks
    private CustomerCreationUseCase customerCreationUseCase;

    @Mock
    private CustomerRepository repository;

    @DisplayName("When creating Customer")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                org.mockito.Mockito.lenient().when(repository.save(any()))
                        .thenAnswer(invocationOnMock -> {
                            Customer customer = invocationOnMock.getArgument(0);
                            setField(customer, "id", fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577"));
                            return customer;
                        });
            }

            @DisplayName("Given a Customer with all fields")
            @Test
            void test1() {
                //Given
                var customer = create_Customer()
                        .withAllFields();
                //When
                var actual = customerCreationUseCase.create(customer);
                //Then
                assertThat(actual.getId())
                        .hasToString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577");
            }

            @DisplayName("Given an existing customer with same id, should update and return existing")
            @Test
            void testIdempotency() {
                //Given
                var customerId = fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577");
                var existing = create_Customer().withAllFields();
                setField(existing, "id", customerId);
                var incoming = create_Customer().withAllFields();
                setField(incoming, "id", customerId);
                setField(incoming, "name", "Updated Name");

                org.mockito.Mockito.when(repository.findByDocument(incoming.getDocument()))
                        .thenReturn(java.util.Optional.of(existing));

                //When
                var actual = customerCreationUseCase.create(incoming);

                //Then
                assertThat(actual.getId()).isEqualTo(customerId);
                org.mockito.Mockito.verify(repository).save(existing);
            }

            @DisplayName("Given an existing customer with different id, should reconcile identity")
            @Test
            void testReconciliation() {
                //Given
                var oldId = fromString("11111111-1111-1111-1111-111111111111");
                var newId = fromString("22222222-2222-2222-2222-222222222222");
                var existing = create_Customer().withAllFields();
                setField(existing, "id", oldId);
                var incoming = create_Customer().withAllFields();
                setField(incoming, "id", newId);

                org.mockito.Mockito.when(repository.findByDocument(incoming.getDocument()))
                        .thenReturn(java.util.Optional.of(existing));
                org.mockito.Mockito.when(repository.findById(newId))
                        .thenReturn(java.util.Optional.of(incoming));

                //When
                var actual = customerCreationUseCase.create(incoming);

                //Then
                assertThat(actual.getId()).isEqualTo(newId);
                org.mockito.Mockito.verify(repository).updateIdentity(oldId, newId, incoming.getName(), incoming.getEmail());
            }
        }
    }
}