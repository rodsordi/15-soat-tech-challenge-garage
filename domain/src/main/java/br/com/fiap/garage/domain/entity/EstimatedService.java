package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.now;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "id")
@Entity
@Table(schema = "garage")
public class EstimatedService extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(comment = "Estimated Service id. Owner: db")
    private Long id;

    @Column(nullable = false, comment = "Original service id. Owner: self")
    private UUID serviceId;

    @Column(nullable = false, comment = "Estimated Service name. Owner: self")
    private String name;

    @Column(comment = "Estimated Service description. Owner: self")
    private String description;

    @Column(nullable = false, comment = "Estimated Service cost. Owner: self")
    private BigDecimal cost;

    @Column(comment = "Estimated Service finished at. Owner: self")
    private LocalDateTime finishedAt;

    // Value Object
    @Singular(value = "estimatedMaterial", ignoreNullCollections = true)
    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "estimated_service_id", updatable = false, nullable = false)
    @OrderBy("createdAt desc")
    private Set<EstimatedMaterial> estimatedMaterials;

    public void finish() {
        finishedAt = now();
    }
}
