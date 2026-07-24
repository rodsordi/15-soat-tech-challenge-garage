package br.com.fiap.garage.domain.filter;

import br.com.fiap.commons.filter.AuditableFilter;
import br.com.fiap.garage.domain.entity.Customer;
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
public class CustomerFilter extends AuditableFilter<Customer> implements Specification<Customer> {

    @Schema(example = "00123456000190", description = "Customer document.")
    private String document;

    private Specification<Customer> documentEqual() {
        return (root, query, builder) -> isEmpty(document) ? null :
                builder.equal(root.get("document"), document);
    }

    @Schema(example = "John", description = "Customer name.")
    private String name;

    private Specification<Customer> nameEqual() {
        return (root, query, builder) -> isEmpty(name) ? null :
                builder.equal(root.get("name"), name);
    }

    @Schema(example = "user@email.com", description = "Customer e-mail.")
    private String email;

    private Specification<Customer> emailEqual() {
        return (root, query, builder) -> isEmpty(email) ? null :
                builder.equal(root.get("email"), email);
    }

    @Override
    public @Nullable Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return super.buildSpecification()
                .and(documentEqual())
                .and(nameEqual())
                .and(emailEqual())
                .toPredicate(root, query, criteriaBuilder);
    }
}
