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
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("When creating Customer")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.save(any()))
                        .thenAnswer(invocationOnMock -> {
                            Customer customer = invocationOnMock.getArgument(0);
                            setField(customer, "id", fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577"));
                            return customer;
                        });
                when(passwordEncoder.encode(any()))
                        .thenAnswer(invocation -> invocation.getArgument(0));
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
        }
    }
}