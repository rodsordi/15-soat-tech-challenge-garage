package br.com.fiap.garage.domain.filter;

import br.com.fiap.commons.filter.AuditableFilter;
import br.com.fiap.garage.domain.entity.Employee;
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
public class EmployeeFilter extends AuditableFilter<Employee> implements Specification<Employee> {

    @Schema(example = "123.456.789-10", description = "Employee cpf.")
    private String cpf;

    private Specification<Employee> cpfEqual() {
        return (root, query, builder) -> isEmpty(cpf) ? null :
                builder.equal(root.get("cpf"), cpf);
    }

    @Schema(example = "John", description = "Employee name.")
    private String name;

    private Specification<Employee> nameEqual() {
        return (root, query, builder) -> isEmpty(name) ? null :
                builder.equal(root.get("name"), name);
    }

    @Schema(example = "user@email.com", description = "Employee e-mail.")
    private String email;

    private Specification<Employee> emailEqual() {
        return (root, query, builder) -> isEmpty(email) ? null :
                builder.equal(root.get("email"), email);
    }

    @Override
    public @Nullable Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return super.buildSpecification()
                .and(cpfEqual())
                .and(nameEqual())
                .and(emailEqual())
                .toPredicate(root, query, criteriaBuilder);
    }
}
