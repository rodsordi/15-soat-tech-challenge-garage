package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.filter.InventoryMaterialFilter;
import br.com.fiap.garage.domain.repository.InventoryMaterialRepository;
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

import static br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory.create_InventoryMaterial;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryMaterialSearchUseCaseTest {

    @InjectMocks
    private InventoryMaterialSearchUseCase inventoryMaterialSearchUseCase;

    @Mock
    private InventoryMaterialRepository repository;

    @DisplayName("When finding InventoryMaterial by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.findById(any()))
                        .thenReturn(Optional.of(create_InventoryMaterial()
                                .withAllFields()));
            }

            @DisplayName("Given a valid inventoryMaterial id")
            @Test
            void test1() {
                //Given
                var inventoryMaterialId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = inventoryMaterialSearchUseCase.findById(inventoryMaterialId);
                //Then
                assertThat(actual)
                        .isNotNull();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a inventoryMaterialId, in scenario with no registered InventoryMaterial")
            @Test
            void test1() {
                //Given
                var inventoryMaterialId = UUID.fromString("fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2");
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> inventoryMaterialSearchUseCase.findById(inventoryMaterialId));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [InventoryMaterial] with [id]: [fbd817e7-64f5-4e0a-80fe-51a6eb35e9a2] not found");
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
                                create_InventoryMaterial().withAllFields(),
                                create_InventoryMaterial().withAllFields()
                        )));
            }

            @DisplayName("Given a valid inventory filter")
            @Test
            void test1() {
                //Given
                var inventoryFilter = new InventoryMaterialFilter();
                //When
                var actual = inventoryMaterialSearchUseCase.findAll(inventoryFilter);
                //Then
                assertThat(actual)
                        .isNotEmpty();
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given an empty InventoryMaterial filter, in scenario with no registered InventoryMaterial")
            @Test
            void test1() {
                //Given
                var inventoryMaterialFilter = new InventoryMaterialFilter();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> inventoryMaterialSearchUseCase.findAll(inventoryMaterialFilter));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [InventoryMaterial] not found");
            }
        }
    }
}