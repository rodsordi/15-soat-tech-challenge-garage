package br.com.fiap.garage.domain.entity;

import br.com.fiap.commons.entity.AuditableEntity;
import br.com.fiap.commons.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static java.lang.String.format;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = "id")
@Entity
@Table(schema = "garage")
public class InventoryMaterial extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(comment = "Inventory id. Owner: db")
    private UUID id;

    @Builder.Default
    @Column(nullable = false, comment = "Inventory quantity in stock. Owner: self")
    private Integer quantityInStock = 0;

    @Builder.Default
    @Column(nullable = false, comment = "Inventory reserved quantity. Owner: self")
    private Integer reservedQuantity = 0;

    // Value Object
    @OneToOne(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    @MapsId
    private Material material;

    public void update(InventoryMaterial inventoryMaterial) {
        if (inventoryMaterial == null)
            return;

        if (inventoryMaterial.quantityInStock != null)
            this.quantityInStock = inventoryMaterial.quantityInStock;

        if (inventoryMaterial.material != null)
            update(inventoryMaterial.material);
    }

    private void update(Material material) {
        if (this.material == null)
            this.material = material;
        else
            this.material.update(material);
    }

    public void addQuantityToStock(int quantityToBeAddedToStock) {
        quantityInStock += quantityToBeAddedToStock;
    }

    public void reserveQuantity(int quantityToBeReserved) {
        if (quantityToBeReserved <= 0)
            throw new BusinessException("You are trying to reserve an empty or negative quantity.");

        if (quantityToBeReserved > quantityInStock)
            throw new BusinessException(format("Cannot reserve %s quantity, because there are only %s quantity in stock.",
                    quantityToBeReserved,
                    quantityInStock));

        if (quantityToBeReserved > quantityInStock - this.reservedQuantity)
            throw new BusinessException(format("Cannot reserve %s quantity, because there are already too many reserved quantities reserved: %s in a stock with just %s quantity.",
                    quantityToBeReserved,
                    this.reservedQuantity,
                    quantityInStock));

        this.reservedQuantity += quantityToBeReserved;
    }

    public void concludeReservedQuantity(int reservedQuantityToBeConcluded) {
        if (reservedQuantityToBeConcluded <= 0)
            throw new BusinessException("You are trying to conclude an empty or negative reserved quantity.");

        if (reservedQuantityToBeConcluded > this.reservedQuantity)
            throw new BusinessException(format("You are trying to conclude %s quantity, that is bigger then the current reserved quantity: %s.",
                    reservedQuantityToBeConcluded,
                    this.reservedQuantity));

        if (reservedQuantityToBeConcluded > this.quantityInStock)
            throw new BusinessException(format("You are trying to conclude %s quantity, that is bigger then the current stock quantity: %s.",
                    reservedQuantityToBeConcluded,
                    this.quantityInStock));

        this.reservedQuantity -= reservedQuantityToBeConcluded;
        this.quantityInStock -= reservedQuantityToBeConcluded;
    }
}
