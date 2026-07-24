package br.com.fiap.commons.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.time.LocalTime.MAX;

@Getter
@Setter
public abstract class AuditableFilter<T> extends PagedFilter {

    @JsonFormat(pattern = "yyyy-MM-dd", shape = STRING)
    @Schema(example = "2025-01-01",
            format = "date",
            description = "Register created at (from).")
    private LocalDate createdAtFrom;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = STRING)
    @Schema(example = "2025-12-31",
            format = "date",
            description = "Register created at (to).")
    private LocalDate createdAtTo;

    private Specification<T> createdAtFromGreaterThanOrEqualTo() {
        return (root, query, builder) -> createdAtFrom == null ? null :
                builder.greaterThanOrEqualTo(root.get("createdAt"), createdAtFrom);
    }

    private Specification<T> createdAtToLessThanOrEqualTo() {
        return (root, query, builder) -> createdAtTo == null ? null :
                builder.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.of(createdAtTo, MAX));
    }

    protected Specification<T> buildSpecification() {
        return createdAtFromGreaterThanOrEqualTo()
                .and(createdAtToLessThanOrEqualTo());
    }
}
