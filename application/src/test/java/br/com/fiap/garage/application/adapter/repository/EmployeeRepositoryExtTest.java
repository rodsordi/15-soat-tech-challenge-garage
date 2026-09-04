package br.com.fiap.garage.application.adapter.repository;

import br.com.fiap.commons.config.JpaConfig;
import br.com.fiap.garage.domain.entity.Employee;
import br.com.fiap.garage.domain.filter.EmployeeFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static br.com.fiap.commons.util.DateUtil.newDate;
import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.EmployeeFactory.create_Employee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = JpaConfig.class)
class EmployeeRepositoryExtTest {

    @Autowired
    private EmployeeRepositoryExt repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("When finding all employees")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a filter with all fields, in scenario with registers")
            @Test
            void test1() {
                //Scenario
                var employee = create_Employee()
                        .withAllFieldsExceptDB();
                employee.getAuthorities()
                        .forEach(authority -> em.persist(authority));
                employee = em.merge(employee);
                em.flush();
                setField(employee, "createdAt", newDateTime("13/12/2026 23:59:59"));
                em.flush();
                //Given
                var filter = new EmployeeFilter();
                filter.setCpf("17902652075");
                filter.setName("John Doe");
                filter.setEmail("john.doe@garage.com");
                filter.setCreatedAtFrom(newDate("13/12/2026"));
                filter.setCreatedAtTo(newDate("13/12/2026"));
                assertThatObject(filter)
                        .hasNoEmptyFields();
                //When
                var actual = repository.findAll(filter, filter.buildPageRequest());
                //Then
                assertThat(actual)
                        .hasSize(1)
                        .extracting(
                                Employee::getCpf,
                                Employee::getName,
                                Employee::getEmail)
                        .containsExactly(tuple(
                                "17902652075",
                                "John Doe",
                                "john.doe@garage.com"));
            }
        }
    }

    @DisplayName("When finding employee by cpf")
    @Nested
    class FindByCpf {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a valid cpf, in scenario with register")
            @Test
            void test1() {
                //Scenario
                var employee = create_Employee()
                        .withAllFieldsExceptDB();
                employee.getAuthorities()
                        .forEach(authority -> em.persist(authority));
                setField(employee, "cpf", "03739169060");
                repository.save(employee);
                em.flush();
                //Given
                var cpf = "03739169060";
                //When
                var actual = repository.findByCpf(cpf);
                //Then
                assertThat(actual)
                        .isPresent()
                        .get()
                        .extracting(Employee::getCpf)
                        .isEqualTo("03739169060");
            }
        }
    }
}