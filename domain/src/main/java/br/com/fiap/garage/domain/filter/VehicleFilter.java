package br.com.fiap.garage.domain.filter;

import br.com.fiap.commons.filter.AuditableFilter;
import br.com.fiap.garage.domain.entity.Vehicle;
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

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Getter
@Setter
@ParameterObject
public class VehicleFilter extends AuditableFilter<Vehicle> implements Specification<Vehicle> {

    @Schema(example = "Volkswagen", description = "Vehicle make.")
    private String make;

    private Specification<Vehicle> makeEqual() {
        return (root, query, builder) -> isEmpty(make) ? null :
                builder.equal(root.get("make"), make);
    }

    @Schema(example = "Gol", description = "Vehicle model.")
    private String model;

    private Specification<Vehicle> modelEqual() {
        return (root, query, builder) -> isEmpty(model) ? null :
                builder.equal(root.get("model"), model);
    }

    @Schema(example = "ABC1C34", description = "Vehicle license plate.")
    private String licensePlate;

    private Specification<Vehicle> licensePlateEqual() {
        return (root, query, builder) -> isEmpty(licensePlate) ? null :
                builder.equal(root.get("licensePlate"), licensePlate);
    }

    @Override
    public @Nullable Predicate toPredicate(Root<Vehicle> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return super.buildSpecification()
                .and(makeEqual())
                .and(modelEqual())
                .and(licensePlateEqual())
                .toPredicate(root, query, criteriaBuilder);
    }
}
