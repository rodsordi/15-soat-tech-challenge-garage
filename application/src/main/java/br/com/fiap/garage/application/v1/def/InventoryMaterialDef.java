package br.com.fiap.garage.application.v1.def;

import br.com.fiap.commons.def.AuditableDef;
import br.com.fiap.garage.application.v1.controller.InventoryMaterialController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * | dto            | Request     | Response             |
 * | Representation | Represented | RepresentedPersisted |
 * | Detailed       | Detailed    | DetailedPersisted    |
 */
public interface InventoryMaterialDef {

    interface Represented {

        @Schema(example = "100", description = "Inventory quantity in stock. Owner: self")
        @NotNull
        Integer getQuantityInStock();
    }

    interface Detailed extends Represented {

    }

    interface RepresentedPersisted extends AuditableDef.RepresentedPersisted {

        @JsonProperty(index = 1)
        @Schema(example = "4ee13743-56d1-4e66-8ef0-cf2c8c45d847", description = "Inventory id. Owner: db")
        @NotNull
        UUID getId();

        @Schema(example = "1", description = "Inventory reserved quantity. Owner: self")
        @NotNull
        Integer getReservedQuantity();
    }

    interface DetailedPersisted extends AuditableDef.DetailedPersisted, RepresentedPersisted {

    }

    interface Request extends Detailed {

        <T extends MaterialDef.Request> T getMaterial();
    }

    interface Response extends Detailed, DetailedPersisted {

        <T extends MaterialDef.Response> T getMaterial();
    }

    interface Representation extends Represented, RepresentedPersisted {

        <T extends MaterialDef.Representation> T getMaterial();

        @JsonIgnore
        default Class<?> getControllerClass() {
            return InventoryMaterialController.class;
        }
    }

    interface PutRequest extends Request {

    }

    interface PatchRequest {

        @Schema(example = "100", description = "Inventory quantity to be added to stock. Owner: self")
        Integer getQuantityToBeAddedToStock();

        @Schema(example = "1", description = "Inventory quantity to be reserved. Owner: self")
        Integer getQuantityToBeReserved();

        @Schema(example = "1", description = "Inventory reserved quantity to be concluded. Owner: self")
        Integer getReservedQuantityToBeConcluded();
    }
}
