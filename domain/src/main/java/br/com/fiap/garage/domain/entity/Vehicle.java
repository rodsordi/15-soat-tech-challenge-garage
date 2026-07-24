package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import br.com.fiap.commons.validation.LicensePlate;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Year;
import java.util.UUID;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class Vehicle extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(comment = "Vehicle id. Owner: db")
    private UUID id;

    @Column(nullable = false, length = 100, comment = "Vehicle make. Owner: self")
    private String make;

    @Column(nullable = false, length = 100, comment = "Vehicle model. Owner: self")
    private String model;

    @LicensePlate
    @Column(nullable = false, length = 10, comment = "Vehicle license plate. Owner: self")
    private String licensePlate;

    @Column(nullable = false, comment = "Vehicle manufacture year. Owner: self")
    private Year manufactureYear;

    // Aggregate (bi-directional)
    @Setter
    @ManyToOne(cascade = {MERGE, PERSIST})
    @JoinColumn(updatable = false, comment = "Customer id. Owner: db")
    private Customer customer;
}
