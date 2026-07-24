package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.filter.CustomerFilter;
import br.com.fiap.garage.domain.repository.CustomerRepository;
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

import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerSearchUseCaseTest {

    @InjectMocks
    private CustomerSearchUseCase customerSearchUseCase;

    @Mock
    private CustomerRepository repository;

    @DisplayName("When finding Customer by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_Customer()
                                .withAllFields()));
            }

            @DisplayName("Given a valid customer id")
            @Test
            void test1() {
                //Given
                var customerId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = customerSearchUseCase.findById(customerId);
                //Then
                assertThat(actual)
                        .isNotNull();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a customerId, in scenario with no registered Customer")
            @Test
            void test1() {
                //Given
                var customerId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> customerSearchUseCase.findById(customerId));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Customer] with [id]: [fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2] not found");
            }
        }
    }

    @DisplayName("When finding all Customers")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findAll(any(), any()))
                        .thenReturn(new PageImpl<>(List.of(create_Customer()
                                .withAllFields())));
            }

            @DisplayName("Given an empty Customer filter")
            @Test
            void test1() {
                //Given
                var customerFilter = new CustomerFilter();
                //When
                var actual = customerSearchUseCase.findAll(customerFilter);
                //Then
                assertThat(actual)
                        .isNotEmpty();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given an empty Customer filter, in scenario with no registered Customer")
            @Test
            void test1() {
                //Given
                var customerFilter = new CustomerFilter();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> customerSearchUseCase.findAll(customerFilter));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Customer] not found");
            }
        }
    }
}