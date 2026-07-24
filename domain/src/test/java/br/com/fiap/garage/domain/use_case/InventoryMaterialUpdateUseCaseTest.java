package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.repository.InventoryMaterialRepository;
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

import static br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory.create_InventoryMaterial;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryMaterialUpdateUseCaseTest {

    @InjectMocks
    private InventoryMaterialUpdateUseCase inventoryMaterialUpdateUseCase;

    @Mock
    private InventoryMaterialRepository repository;

    @DisplayName("When updating InventoryMaterial")
    @Nested
    class Update {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_InventoryMaterial()
                                .withAllFields()));
                when(repository.save(any()))
                        .thenAnswer(i -> i.getArgument(0));
            }

            @DisplayName("Given a valid id, and a InventoryMaterial with all fields")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var inventoryMaterial = create_InventoryMaterial()
                        .withAllFields();
                //When
                var actual = inventoryMaterialUpdateUseCase.update(id, inventoryMaterial);
                //Then
                assertThat(actual).isNotNull();
                verify(repository, times(1)).save(any());
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a InventoryMaterial with all fields")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var inventoryMaterial = create_InventoryMaterial()
                        .withAllFields();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> inventoryMaterialUpdateUseCase.update(id, inventoryMaterial));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [InventoryMaterial] with [id]: [b06b216f-215d-41eb-8c03-570f03562064] not found");
            }
        }
    }

    @DisplayName("When adding quantity to stock")
    @Nested
    class AddQuantityToStock {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_InventoryMaterial()
                                .withAllFields()));
                when(repository.save(any()))
                        .thenAnswer(i -> i.getArgument(0));
            }

            @DisplayName("Given a valid id, and a valid quantityToBeAddedToStock")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var quantityToBeAddedToStock = 1;
                //When
                var actual = inventoryMaterialUpdateUseCase.addQuantityToStock(id, quantityToBeAddedToStock);
                //Then
                assertThat(actual).isNotNull();
                verify(repository, times(1)).save(any());
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a quantityToBeAddedToStock")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var quantityToBeAddedToStock = 1;
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> inventoryMaterialUpdateUseCase.addQuantityToStock(id, quantityToBeAddedToStock));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [InventoryMaterial] with [id]: [b06b216f-215d-41eb-8c03-570f03562064] not found");
            }
        }
    }

    @DisplayName("When adding reserved quantity")
    @Nested
    class AddReservedQuantity {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_InventoryMaterial()
                                .withAllFields()));
                when(repository.save(any()))
                        .thenAnswer(i -> i.getArgument(0));
            }

            @DisplayName("Given a valid id, and a valid quantityToBeReserved")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var quantityToBeReserved = 1;
                //When
                var actual = inventoryMaterialUpdateUseCase.addReservedQuantity(id, quantityToBeReserved);
                //Then
                assertThat(actual).isNotNull();
                verify(repository, times(1)).save(any());
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a quantityToBeReserved")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var quantityToBeReserved = 1;
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> inventoryMaterialUpdateUseCase.addReservedQuantity(id, quantityToBeReserved));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [InventoryMaterial] with [id]: [b06b216f-215d-41eb-8c03-570f03562064] not found");
            }
        }
    }

    @DisplayName("When concluding reserved quantity")
    @Nested
    class ConcludeReservedQuantity {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_InventoryMaterial()
                                .withAllFields()));
                when(repository.save(any()))
                        .thenAnswer(i -> i.getArgument(0));
            }

            @DisplayName("Given a valid id, and a valid reservedQuantityToBeConcluded")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var reservedQuantityToBeConcluded = 1;
                //When
                var actual = inventoryMaterialUpdateUseCase.concludeReservedQuantity(id, reservedQuantityToBeConcluded);
                //Then
                assertThat(actual).isNotNull();
                verify(repository, times(1)).save(any());
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a reservedQuantityToBeConcluded")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var reservedQuantityToBeConcluded = 1;
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> inventoryMaterialUpdateUseCase.concludeReservedQuantity(id, reservedQuantityToBeConcluded));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [InventoryMaterial] with [id]: [b06b216f-215d-41eb-8c03-570f03562064] not found");
            }
        }
    }
}