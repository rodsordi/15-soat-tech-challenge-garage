package br.com.fiap.garage.domain.entity.factory;

import br.com.fiap.garage.domain.entity.Employee;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.AuthorityFactory.create_Authority;
import static java.util.UUID.fromString;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmployeeFactory {

    private final Employee.EmployeeBuilder<?, ?> builder;

    public static EmployeeFactory create_Employee() {
        return new EmployeeFactory(Employee.builder());
    }

    public Employee withAllFields() {
        var result = builder
                // Inheritance (User)
                .id(fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d"))
                .username("john.doe@garage.com")
                .password("abc123")
                .name("John Doe")
                .email("john.doe@garage.com")
                // Self
                .cpf("17902652075")
                // Composition
                .authority(create_Authority().withAllFields())
                // Inheritance (AuditableEntity)
                .createdAt(newDateTime("13/12/2026 23:59:59"))
                .updatedAt(newDateTime("14/12/2026 23:59:59"))
                .build();

        // Validation check
        assertThatObject(result)
                .hasNoEmptyFields();
        return result;
    }

    public Employee withAllFieldsExceptDB() {
        withAllFields();
        return builder
                .id(null)
                .clearAuthorities()
                .authority(create_Authority().withAllFieldsExceptDB())
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public Employee valid() {
        return builder
                .name("Jane Smith")
                .build();
    }

    public Employee initiatedEmpty() {
        return builder.build();
    }
}