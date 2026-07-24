package br.com.fiap.garage.domain.filter;

import br.com.fiap.commons.filter.AuditableFilter;
import br.com.fiap.garage.domain.entity.Service;
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
public class ServiceFilter extends AuditableFilter<Service> implements Specification<Service> {

    @Schema(example = "Oil", description = "Service name.")
    private String name;

    private Specification<Service> nameEqual() {
        return (root, query, builder) -> isEmpty(name) ? null :
                builder.equal(root.get("name"), name);
    }

    @Schema(example = "1.99", description = "Service cost (from).")
    private BigDecimal costFrom;

    private Specification<Service> costFrom() {
        return (root, query, builder) -> costFrom == null ? null :
                builder.greaterThanOrEqualTo(root.get("cost"), costFrom);
    }

    @Schema(example = "9.99", description = "Service cost (to).")
    private BigDecimal costTo;

    private Specification<Service> costTo() {
        return (root, query, builder) -> costTo == null ? null :
                builder.lessThanOrEqualTo(root.get("cost"), costTo);
    }

    @Override
    public @Nullable Predicate toPredicate(Root<Service> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return super.buildSpecification()
                .and(nameEqual())
                .and(costFrom())
                .and(costTo())
                .toPredicate(root, query, criteriaBuilder);
    }
}
