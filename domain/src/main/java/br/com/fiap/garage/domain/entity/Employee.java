package br.com.fiap.garage.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "garage")
public class Employee extends User implements Serializable {

    @CPF
    @Column(nullable = false, length = 11, unique = true, comment = "Employee cpf. Owner: self")
    private String cpf;
}
