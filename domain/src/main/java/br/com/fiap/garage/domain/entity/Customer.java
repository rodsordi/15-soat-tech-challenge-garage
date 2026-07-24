package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.validation.CpfOrCnpj;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "garage")
public class Customer extends User implements Serializable {

    @CpfOrCnpj
    @Column(nullable = false, length = 14, unique = true, comment = "Customer document (CPF/CNPJ). Owner: self")
    private String document;

    // Value Object (bi-directional)
    @Singular(value = "vehicle", ignoreNullCollections = true)
    @OneToMany(mappedBy = "customer", cascade = ALL, orphanRemoval = true)
    @OrderBy("createdAt desc")
    private Set<Vehicle> vehicles;
}
