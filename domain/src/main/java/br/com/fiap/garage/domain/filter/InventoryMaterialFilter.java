package br.com.fiap.garage.domain.filter;

import br.com.fiap.commons.filter.AuditableFilter;
import br.com.fiap.garage.domain.entity.InventoryMaterial;
import br.com.fiap.garage.domain.enums.MaterialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Getter
@Setter
@ParameterObject
public class InventoryMaterialFilter extends AuditableFilter<InventoryMaterial> implements Specification<InventoryMaterial> {

    public static final String MATERIAL = "material";

    @Schema(example = "SPARE_PART", description = "Inventory Material type.")
    private MaterialType type;

    private Specification<InventoryMaterial> typeEqual() {
        return (root, query, builder) -> type == null ? null :
                builder.equal(root.get(MATERIAL).get("type"), type);
    }

    @Schema(example = "Tire", description = "Inventory Material name.")
    private String name;

    private Specification<InventoryMaterial> nameEqual() {
        return (root, query, builder) -> isEmpty(name) ? null :
                builder.equal(root.get(MATERIAL).get("name"), name);
    }

    @Schema(example = "1.99", description = "Inventory Material cost (from).")
    private BigDecimal costFrom;

    private Specification<InventoryMaterial> costFrom() {
        return (root, query, builder) -> costFrom == null ? null :
                builder.greaterThanOrEqualTo(root.get(MATERIAL).get("cost"), costFrom);
    }

    @Schema(example = "9.99", description = "Inventory Material cost (to).")
    private BigDecimal costTo;

    private Specification<InventoryMaterial> costTo() {
        return (root, query, builder) -> costTo == null ? null :
                builder.lessThanOrEqualTo(root.get(MATERIAL).get("cost"), costTo);
    }

    @Override
    public @Nullable Predicate toPredicate(Root<InventoryMaterial> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return super.buildSpecification()
                .and(typeEqual())
                .and(nameEqual())
                .and(costFrom())
                .and(costTo())
                .toPredicate(root, query, criteriaBuilder);
    }
}
