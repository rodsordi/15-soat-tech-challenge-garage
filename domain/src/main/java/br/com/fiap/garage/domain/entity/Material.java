package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import br.com.fiap.garage.domain.enums.MaterialType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class Material extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(comment = "Material id. Owner: db")
    private UUID id;

    @Enumerated(STRING)
    @Column(nullable = false, length = 55, comment = "Material type. Owner: self")
    private MaterialType type;

    @Column(nullable = false, comment = "Material name. Owner: self")
    private String name;

    @Column(comment = "Material description. Owner: self")
    private String description;

    @Column(nullable = false, comment = "Material cost. Owner: self")
    private BigDecimal cost;

    public void update(Material material) {
        if (material == null)
            return;

        if (material.type != null)
            this.type = material.type;

        if (material.name != null)
            this.name = material.name;

        if (material.description != null)
            this.description = material.description;

        if (material.cost != null)
            this.cost = material.cost;
    }
}
