package br.com.fiap.garage.infra.repository;

import br.com.fiap.commons.config.JpaConfig;
import br.com.fiap.garage.domain.entity.InventoryMaterial;
import br.com.fiap.garage.domain.entity.Material;
import br.com.fiap.garage.domain.filter.InventoryMaterialFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDate;
import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory.create_InventoryMaterial;
import static br.com.fiap.garage.domain.enums.MaterialType.SHOP_SUPPLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = JpaConfig.class)
class InventoryMaterialRepositoryExtTest {

    @Autowired
    private InventoryMaterialRepositoryExt repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("When finding all employees")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a filter with all fields, in scenario with registers")
            @Test
            void test1() {
                //Scenario
                var inventoryMaterial = create_InventoryMaterial()
                        .withAllFieldsExceptDB();
                inventoryMaterial = em.merge(inventoryMaterial);
                em.flush();
                setField(inventoryMaterial, "createdAt", newDateTime("13/12/2026 23:59:59"));
                em.flush();
                //Given
                var filter = new InventoryMaterialFilter();
                filter.setType(SHOP_SUPPLY);
                filter.setName("Engine Oil");
                filter.setCostFrom(new BigDecimal("150.00"));
                filter.setCostTo(new BigDecimal("150.00"));
                filter.setCreatedAtFrom(newDate("13/12/2026"));
                filter.setCreatedAtTo(newDate("13/12/2026"));
                assertThatObject(filter)
                        .hasNoEmptyFields();
                //When
                var actual = repository.findAll(filter, filter.buildPageRequest());
                //Then
                assertThat(actual)
                        .hasSize(1)
                        .extracting(InventoryMaterial::getMaterial)
                        .extracting(
                                Material::getType,
                                Material::getName,
                                Material::getCost)
                        .containsExactly(tuple(
                                SHOP_SUPPLY,
                                "Engine Oil",
                                new BigDecimal("150.00")));
            }
        }
    }
}