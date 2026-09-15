package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.com.fiap.garage.domain.enums.WorkOrderStatus.RECEIVED;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.EnumType.STRING;
import static java.math.BigDecimal.ZERO;
import static java.time.LocalDateTime.now;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class WorkOrder extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(comment = "Work Order id. Owner: db")
    private UUID id;

    @Builder.Default
    @Enumerated(STRING)
    @Column(comment = "Work Order status. Owner: self")
    private WorkOrderStatus status = RECEIVED;

    @Column(nullable = false, comment = "Work Order total amount estimation. Owner: self")
    private BigDecimal totalAmount;

    // Aggregate
    @ManyToOne(cascade = {MERGE, PERSIST})
    @JoinColumn(updatable = false, comment = "Vehicle id. Owner: db")
    private Vehicle vehicle;

    // Aggregate
    @ManyToOne(cascade = {MERGE, PERSIST})
    @JoinColumn(updatable = false, comment = "Employee id. Owner: db")
    private Employee employee;

    // Value Object
    @Singular(value = "estimatedService", ignoreNullCollections = true)
    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "work_order_id", updatable = false, nullable = false, comment = "Work Order id. Owner: db")
    @OrderBy("createdAt desc")
    @Valid
    private Set<EstimatedService> estimatedServices;

    @Column(comment = "Work Order diagnosing start timestamp. Owner: self")
    private LocalDateTime diagnosingAt;

    @Column(comment = "Work Order waiting approval timestamp. Owner: self")
    private LocalDateTime waitingApprovalAt;

    @Column(comment = "Work Order executing start timestamp. Owner: self")
    private LocalDateTime executingAt;

    @Column(comment = "Work Order finished timestamp. Owner: self")
    private LocalDateTime finishedAt;

    @Column(comment = "Work Order released timestamp. Owner: self")
    private LocalDateTime releasedAt;

    public void update(Employee employee) {
        this.employee = employee;
    }

    public void update(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void update(Set<Service> services) {
        estimatedServices = services.stream()
                .map(Service::buildEstimatedService)
                .collect(Collectors.toSet());
    }

    public void calculateTotalAmount() {
        var serviceTotalAmount = estimatedServices.stream()
                .map(EstimatedService::getCost)
                .reduce(ZERO, BigDecimal::add);

        var estimatedMaterials = estimatedServices.stream()
                .map(EstimatedService::getEstimatedMaterials)
                .flatMap(Collection::stream)
                .toList();

        var materialTotalAmount = estimatedMaterials.stream()
                .map(EstimatedMaterial::getCost)
                .reduce(ZERO, BigDecimal::add);

        totalAmount = serviceTotalAmount.add(materialTotalAmount);
    }

    public void diagnose() {
        status = status.getState()
                .apply(this)
                .diagnose();
        diagnosingAt = now();
    }

    public void waitForApproval() {
        status = status.getState()
                .apply(this)
                .waitForApproval();
        waitingApprovalAt = now();
    }

    public void execute() {
        status = status.getState()
                .apply(this)
                .execute();
        executingAt = now();
    }

    public void finish() {
        status = status.getState()
                .apply(this)
                .finish();
        finishedAt = now();
    }

    public void release() {
        status = status.getState()
                .apply(this)
                .release();
        releasedAt = now();
    }

    public Duration calculateDiagnosingDuration() {
        var start = diagnosingAt != null ? diagnosingAt : getCreatedAt();
        if (start != null && waitingApprovalAt != null) {
            return Duration.between(start, waitingApprovalAt);
        }
        return null;
    }

    public Duration calculateExecutingDuration() {
        var start = executingAt != null ? executingAt : waitingApprovalAt;
        if (start != null && finishedAt != null) {
            return Duration.between(start, finishedAt);
        }
        return null;
    }

    public Duration calculateFinishedDuration() {
        var start = finishedAt != null ? finishedAt : executingAt;
        if (start != null && releasedAt != null) {
            return Duration.between(start, releasedAt);
        }
        return null;
    }
}
