package br.com.fiap.garage.domain.use_case;

import br.com.fiap.garage.domain.entity.InventoryMaterial;
import br.com.fiap.garage.domain.repository.InventoryMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory.create_InventoryMaterial;
import static java.util.UUID.fromString;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class InventoryMaterialCreationUseCaseTest {

    @InjectMocks
    private InventoryMaterialCreationUseCase inventoryMaterialCreationUseCase ;

    @Mock
    private InventoryMaterialRepository repository;

    @DisplayName("When creating InventoryMaterial")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(repository.save(any()))
                        .thenAnswer(invocationOnMock -> {
                            InventoryMaterial inventoryMaterial = invocationOnMock.getArgument(0);
                            setField(inventoryMaterial, "id", fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577"));
                            return inventoryMaterial;
                        });
            }

            @DisplayName("Given a InventoryMaterial with all fields")
            @Test
            void test1() {
                //Given
                var inventoryMaterial = create_InventoryMaterial()
                        .withAllFields();
                //When
                var actual = inventoryMaterialCreationUseCase.create(inventoryMaterial);
                //Then
                assertThat(actual.getId())
                        .hasToString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577");
            }
        }
    }
}