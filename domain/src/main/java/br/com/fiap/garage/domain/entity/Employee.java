package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class Employee extends AuditableEntity implements Serializable {

    @Id
    @Builder.Default
    @Column(comment = "Employee id. Owner: db")
    private UUID id = UUID.randomUUID();

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @Column(nullable = false, comment = "Employee name. Owner: self")
    private String name;

    @Column(nullable = false, unique = true, comment = "Employee e-mail. Owner: self")
    private String email;

    @CPF
    @Column(nullable = false, length = 11, unique = true, comment = "Employee cpf. Owner: self")
    private String cpf;

    public void update(Employee employee) {
        if (employee == null)
            return;

        if (employee.name != null)
            this.name = employee.name;

        if (employee.email != null)
            this.email = employee.email;
    }
}
