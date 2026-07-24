package br.com.fiap.garage.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example = "SPARE_PART",
        description = """
                Material type. Owner: self
                * SPARE_PART: Spare parts required on service repair.
                * SHOP_SUPPLY: Shop supplies use in maintenance.
                """)
public enum MaterialType {

    SPARE_PART, SHOP_SUPPLY
}
