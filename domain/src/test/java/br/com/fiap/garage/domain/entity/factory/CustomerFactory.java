package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.Customer;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.AuthorityFactory.create_Authority;
import static br.com.fiap.garage.domain.entity.factory.VehicleFactory.create_Vehicle;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class CustomerFactory {

    private final Customer.CustomerBuilder<?, ?> builder;

    public static CustomerFactory create_Customer() {
        return new CustomerFactory(Customer.builder());
    }

    public Customer withAllFields() {
        var result = builder
                // Inheritance (User)
                .id(fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"))
                .username("jack.doe@company.com")
                .password("4321abcd")
                .name("John Doe")
                .email("john.doe@fiap.com.br")
                // Self
                .document("27351626000107")
                // Composition
                .vehicle(create_Vehicle().withAllFields())
                .authority(create_Authority().withAllFields())
                // Inheritance (AuditableEntity)
                .createdAt(newDateTime("13/12/2026 23:59:59"))
                .updatedAt(newDateTime("14/12/2026 23:59:59"))
                .build();
        // And
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public Customer withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .clearVehicles()
                .vehicle(create_Vehicle().withAllFieldsExceptDB())
                .clearAuthorities()
                .authority(create_Authority().withAllFieldsExceptDB())
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public Customer valid() {
        return builder
                .name("Jane Smith")
                .email("jane.smith@fiap.com.br")
                .build();
    }

    public Customer initiatedEmpty() {
        return builder.build();
    }
}