package br.com.fiap.garage.domain.filter;

import br.com.fiap.commons.filter.AuditableFilter;
import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
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

import java.util.List;

import static br.com.fiap.garage.domain.enums.WorkOrderStatus.FINISHED;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.RELEASED;

@Getter
@Setter
@ParameterObject
public class WorkOrderFilter extends AuditableFilter<WorkOrder> implements Specification<WorkOrder> {

    @Schema(example = "RECEIVED", description = "WorkOrder status.")
    private WorkOrderStatus status;

    private Specification<WorkOrder> statusNotIn() {
        return (root, query, builder) -> FINISHED.equals(status) || RELEASED.equals(status) ? null :
                builder.not(root.get("status").in(List.of(FINISHED, RELEASED)));
    }

    private Specification<WorkOrder> statusEqual() {
        return (root, query, builder) -> status == null ? null :
                builder.equal(root.get("status"), status);
    }

    @Override
    public @Nullable Predicate toPredicate(Root<WorkOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var statusDescOrder = WorkOrderStatus.ordinalExpression(root.get("status"), criteriaBuilder);
        query.orderBy(
                criteriaBuilder.desc(statusDescOrder),
                criteriaBuilder.desc(root.get("createdAt")));
        return super.buildSpecification()
                .and(statusNotIn())
                .and(statusEqual())
                .toPredicate(root, query, criteriaBuilder);
    }
}
