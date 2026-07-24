package br.com.fiap.garage.domain.entity.assertions;

import br.com.fiap.garage.domain.entity.Employee;
import lombok.RequiredArgsConstructor;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class EmployeeAssertions {

    private final Employee actual;

    public static EmployeeAssertions assertThat_Employee(Employee actual) {
        assertThat(actual).isNotNull();
        return new EmployeeAssertions(spy(actual));
    }

    /**
     * @see br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_EmployeeDto_Request() {
        // Self
        assertThat(actual.getId())
                .isNull();
        assertThat(actual.getUsername())
                .isEqualTo("john.doe@garage.com");
        assertThat(actual.getPassword())
                .isEqualTo("1a2s3d4f");
        assertThat(actual.getName())
                .isEqualTo("John");
        assertThat(actual.getEmail())
                .isEqualTo("john.doe@garage.com");
        assertThat(actual.getCpf())
                .isEqualTo("69005975059");
        assertThat(actual.getAuthorities())
                .isNullOrEmpty();

        // Inheritance (AuditableEntity)
        assertThat(actual.getCreatedAt())
                .isNull();
        assertThat(actual.getUpdatedAt())
                .isNull();

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }

    /**
     * @see br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory
     * .withAllFields()
     */
    public void wasConvertedFrom_WorkOrderDto_Request() {
        // Self
        assertThat(actual.getId())
                .hasToString("22e9c194-562a-4029-9bc1-ed37e0a80966");
        assertThat(actual.getUsername())
                .isNull();
        assertThat(actual.getPassword())
                .isNull();
        assertThat(actual.getName())
                .isNull();
        assertThat(actual.getEmail())
                .isNull();
        assertThat(actual.getCpf())
                .isNull();
        assertThat(actual.getAuthorities())
                .isNullOrEmpty();

        // Inheritance (AuditableEntity)
        assertThat(actual.getCreatedAt())
                .isNull();
        assertThat(actual.getUpdatedAt())
                .isNull();

        // And
        assertThatObject(actual)
                .hasAllGetMethodsVerifiedOnceAtLeast();
    }
}