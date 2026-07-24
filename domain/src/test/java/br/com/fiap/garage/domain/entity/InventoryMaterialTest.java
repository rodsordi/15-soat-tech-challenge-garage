package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static br.com.fiap.garage.domain.entity.assertions.InventoryMaterialAssertions.assertThat_InventoryMaterial;
import static br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory.create_InventoryMaterial;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryMaterialTest {

    private InventoryMaterial inventoryMaterial;

    @DisplayName("When updating InventoryMaterial")
    @Nested
    class Update {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a InventoryMaterial with all fields, in scenario with initiated empty InventoryMaterial")
            @Test
            void test1() {
                //Scenario
                inventoryMaterial = create_InventoryMaterial().initiatedEmpty();
                //Given
                var arg = create_InventoryMaterial().withAllFields();
                //When
                inventoryMaterial.update(arg);
                //Then
                assertThat_InventoryMaterial(inventoryMaterial)
                        .wasUpdatedUsing_InventoryMaterial();
            }
        }
    }

    @DisplayName("When adding quantity to stock in InventoryMaterial")
    @Nested
    class AddQuantityToStock {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given multiples quantities to be added to stock, in scenario with empty InventoryMaterial")
            @Test
            void test1() {
                //Scenario
                inventoryMaterial = InventoryMaterial.builder().build();
                //Given
                var quantityToBeAddedToStock1 = 1;
                //When
                inventoryMaterial.addQuantityToStock(quantityToBeAddedToStock1);
                //Then
                assertThat(inventoryMaterial.getQuantityInStock())
                        .isEqualTo(1);
                //Given
                var quantityToBeAddedToStock2 = 2;
                //When
                inventoryMaterial.addQuantityToStock(quantityToBeAddedToStock2);
                //Then
                assertThat(inventoryMaterial.getQuantityInStock())
                        .isEqualTo(3);
                //Given
                var quantityToBeAddedToStock3 = 3;
                //When
                inventoryMaterial.addQuantityToStock(quantityToBeAddedToStock3);
                //Then
                assertThat(inventoryMaterial.getQuantityInStock())
                        .isEqualTo(6);
            }
        }
    }

    @DisplayName("When reserving quantity in InventoryMaterial")
    @Nested
    class ReserveQuantity {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given multiples quantities to be reserved, in scenario with InventoryMaterial with quantityInStock")
            @Test
            void test1() {
                //Scenario
                inventoryMaterial = InventoryMaterial.builder()
                        .quantityInStock(10)
                        .build();
                //Given
                var quantityToBeReserved1 = 1;
                //When
                inventoryMaterial.reserveQuantity(quantityToBeReserved1);
                //Then
                assertThat(inventoryMaterial.getReservedQuantity())
                        .isEqualTo(1);
                //Given
                var quantityToBeReserved2 = 2;
                //When
                inventoryMaterial.reserveQuantity(quantityToBeReserved2);
                //Then
                assertThat(inventoryMaterial.getReservedQuantity())
                        .isEqualTo(3);
                //Given
                var quantityToBeReserved3 = 3;
                //When
                inventoryMaterial.reserveQuantity(quantityToBeReserved3);
                //Then
                assertThat(inventoryMaterial.getReservedQuantity())
                        .isEqualTo(6);
            }

            @DisplayName("Given {quantityToBeReserved}, in scenario with InventoryMaterial with {quantityInStock} and {reservedQuantity}")
            @CsvSource(value = {
                    "1 | 0 | 1 | 1",
                    "1 | 0 | 2 | 1",
                    "1 | 0 | 3 | 1",
                    "1 | 1 | 2 | 2",
                    "1 | 1 | 3 | 2",
                    "1 | 1 | 4 | 2",
                    "2 | 0 | 2 | 2",
                    "2 | 0 | 3 | 2",
                    "2 | 0 | 4 | 2",
                    "1 | 2 | 3 | 3",
                    "1 | 2 | 4 | 3",
                    "1 | 2 | 5 | 3",
            }, delimiterString = "|")
            @ParameterizedTest
            void test(int quantityToBeReserved, int reservedQuantity, int quantityInStock, int expectedReservedQuantity) {
                //Scenario
                inventoryMaterial = InventoryMaterial.builder()
                        .quantityInStock(quantityInStock)
                        .reservedQuantity(reservedQuantity)
                        .build();
                //When
                inventoryMaterial.reserveQuantity(quantityToBeReserved);
                //Then
                assertThat(inventoryMaterial.getReservedQuantity())
                        .isEqualTo(expectedReservedQuantity);
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given {quantityToBeReserved}, in scenario with InventoryMaterial with {quantityInStock} and {reservedQuantity}")
            @CsvSource(value = {
                    "-1 | 0 | 0 | You are trying to reserve an empty or negative quantity.",
                    "0  | 0 | 1 | You are trying to reserve an empty or negative quantity.",
                    "1  | 0 | 0 | Cannot reserve 1 quantity, because there are only 0 quantity in stock.",
                    "2  | 0 | 1 | Cannot reserve 2 quantity, because there are only 1 quantity in stock.",
                    "1  | 1 | 1 | Cannot reserve 1 quantity, because there are already too many reserved quantities reserved: 1 in a stock with just 1 quantity.",
                    "2  | 1 | 2 | Cannot reserve 2 quantity, because there are already too many reserved quantities reserved: 1 in a stock with just 2 quantity.",
            }, delimiterString = "|")
            @ParameterizedTest
            void test(int quantityToBeReserved, int reservedQuantity, int quantityInStock, String expectedMsg) {
                //Scenario
                inventoryMaterial = InventoryMaterial.builder()
                        .quantityInStock(quantityInStock)
                        .reservedQuantity(reservedQuantity)
                        .build();
                //When
                var actual = assertThrows(BusinessException.class,
                        () -> inventoryMaterial.reserveQuantity(quantityToBeReserved));
                //Then
                assertThat(actual.getMessage())
                        .isEqualTo(expectedMsg);
            }
        }
    }

    @DisplayName("When concluding reserved quantity in InventoryMaterial")
    @Nested
    class ConcludeReservedQuantity {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given multiples reserved quantities to be concluded, in scenario with InventoryMaterial with quantityInStock and reservedQuantity")
            @Test
            void test1() {
                //Scenario
                inventoryMaterial = InventoryMaterial.builder()
                        .quantityInStock(6)
                        .reservedQuantity(6)
                        .build();
                //Given
                var reservedQuantityToBeConcluded1 = 1;
                //When
                inventoryMaterial.concludeReservedQuantity(reservedQuantityToBeConcluded1);
                //Then
                assertThat(inventoryMaterial.getQuantityInStock())
                        .isEqualTo(5);
                assertThat(inventoryMaterial.getReservedQuantity())
                        .isEqualTo(5);
                //Given
                var reservedQuantityToBeConcluded2 = 2;
                //When
                inventoryMaterial.concludeReservedQuantity(reservedQuantityToBeConcluded2);
                //Then
                assertThat(inventoryMaterial.getQuantityInStock())
                        .isEqualTo(3);
                assertThat(inventoryMaterial.getReservedQuantity())
                        .isEqualTo(3);
                //Given
                var reservedQuantityToBeConcluded3 = 3;
                //When
                inventoryMaterial.concludeReservedQuantity(reservedQuantityToBeConcluded3);
                //Then
                assertThat(inventoryMaterial.getQuantityInStock())
                        .isZero();
                assertThat(inventoryMaterial.getReservedQuantity())
                        .isZero();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given {reservedQuantityToBeConcluded}, in scenario with InventoryMaterial with {quantityInStock} and {reservedQuantity}")
            @CsvSource(value = {
                    "-1 | 0 | 0 | You are trying to conclude an empty or negative reserved quantity.",
                    "0  | 0 | 1 | You are trying to conclude an empty or negative reserved quantity.",
                    "1  | 0 | 1 | You are trying to conclude 1 quantity, that is bigger then the current reserved quantity: 0.",
                    "2  | 1 | 2 | You are trying to conclude 2 quantity, that is bigger then the current reserved quantity: 1.",
                    "1  | 1 | 0 | You are trying to conclude 1 quantity, that is bigger then the current stock quantity: 0.",
                    "3  | 3 | 2 | You are trying to conclude 3 quantity, that is bigger then the current stock quantity: 2.",
            }, delimiterString = "|")
            @ParameterizedTest
            void test(int reservedQuantityToBeConcluded, int reservedQuantity, int quantityInStock, String expectedMsg) {
                //Scenario
                inventoryMaterial = InventoryMaterial.builder()
                        .quantityInStock(quantityInStock)
                        .reservedQuantity(reservedQuantity)
                        .build();
                //When
                var actual = assertThrows(BusinessException.class,
                        () -> inventoryMaterial.concludeReservedQuantity(reservedQuantityToBeConcluded));
                //Then
                assertThat(actual.getMessage())
                        .isEqualTo(expectedMsg);
            }
        }
    }
}