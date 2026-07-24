package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.Vehicle;
import lombok.RequiredArgsConstructor;

import java.time.Year;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class VehicleFactory {

    private final Vehicle.VehicleBuilder<?, ?> builder;

    public static VehicleFactory create_Vehicle() {
        return new VehicleFactory(Vehicle.builder());
    }

    public Vehicle withAllFields() {
        var result = builder
                // Self
                .id(fromString("9f8792ea-cf8f-43d1-824f-9f7bc433e404"))
                .make("Toyota")
                .model("Corolla")
                .licensePlate("ABC1234")
                .manufactureYear(Year.parse("2024"))
                // Composition
                .customer(null)
                // Inheritance (AuditableEntity)
                .createdAt(newDateTime("21/04/2026 10:00:00"))
                .updatedAt(newDateTime("21/04/2026 15:30:00"))
                .build();

        // And
        assertThatObject(result)
                .hasNoEmptyFields("customer");
        return result;
    }

    public Vehicle withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public Vehicle valid() {
        return builder
                .make("Honda")
                .model("Civic")
                .licensePlate("XYZ-9876")
                .manufactureYear(Year.parse("2023"))
                .build();
    }

    public Vehicle initiatedEmpty() {
        return builder.build();
    }
}