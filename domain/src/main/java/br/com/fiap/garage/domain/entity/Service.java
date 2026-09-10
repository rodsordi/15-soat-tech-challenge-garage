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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class Service extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(comment = "Service id. Owner: db")
    private UUID id;

    @Column(nullable = false, comment = "Service name. Owner: self")
    private String name;

    @Column(comment = "Service description. Owner: self")
    private String description;

    @Column(nullable = false, comment = "Service cost. Owner: self")
    private BigDecimal cost;

    @Column(comment = "Service average time in minutes. Owner: self")
    private Long averageTimeInMinutes;

    // Aggregate
    @Singular(value = "material", ignoreNullCollections = true)
    @ManyToMany(fetch = EAGER)
    @JoinTable(schema = "garage", name = "service_inventory_material",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "inventory_material_id"))
    @OrderBy("createdAt desc")
    private Set<Material> materials;

    public void update(Service service) {
        if (service == null)
            return;

        if (service.name != null)
            this.name = service.name;

        if (service.description != null)
            this.description = service.description;

        if (service.cost != null)
            this.cost = service.cost;

        if (service.materials != null)
            this.materials = service.materials;
    }

    public EstimatedService buildEstimatedService() {
        Set<EstimatedMaterial> estimatedMaterials = null;
        if (this.materials != null) {
            estimatedMaterials = this.materials.stream()
                    .map(m -> EstimatedMaterial.builder()
                            .materialId(m.getId())
                            .type(m.getType())
                            .name(m.getName())
                            .description(m.getDescription())
                            .cost(m.getCost())
                            .build())
                    .collect(Collectors.toSet());
        }

        return EstimatedService.builder()
                .serviceId(this.id)
                .name(this.name)
                .description(this.description)
                .cost(this.cost)
                .estimatedMaterials(estimatedMaterials)
                .build();
    }

    public void updateMaterialsReference(Set<Material> materials) {
        this.materials = materials;
    }

    public void updateAverageTime(Long averageTimeInMinutes) {
        this.averageTimeInMinutes = averageTimeInMinutes;
    }
}
