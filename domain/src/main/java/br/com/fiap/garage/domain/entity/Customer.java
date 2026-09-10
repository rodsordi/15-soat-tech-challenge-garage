package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import br.com.fiap.commons.validation.CpfOrCnpj;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class Customer extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(comment = "Customer id. Owner: db")
    private UUID id;

    @Column(nullable = false, comment = "Customer name. Owner: self")
    private String name;

    @Column(nullable = false, unique = true, comment = "Customer e-mail. Owner: self")
    private String email;

    @CpfOrCnpj
    @Column(nullable = false, length = 14, unique = true, comment = "Customer document (CPF/CNPJ). Owner: self")
    private String document;

    // Value Object (bi-directional)
    @Singular(value = "vehicle", ignoreNullCollections = true)
    @OneToMany(mappedBy = "customer", cascade = ALL, orphanRemoval = true)
    @OrderBy("createdAt desc")
    private Set<Vehicle> vehicles;

    public void update(Customer customer) {
        if (customer == null)
            return;

        if (customer.name != null)
            this.name = customer.name;

        if (customer.email != null)
            this.email = customer.email;
    }
}
