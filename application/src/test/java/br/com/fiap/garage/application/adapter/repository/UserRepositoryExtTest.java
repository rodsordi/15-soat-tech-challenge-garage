package br.com.fiap.garage.application.adapter.repository;

import br.com.fiap.commons.config.JpaConfig;
import br.com.fiap.garage.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = JpaConfig.class)
class UserRepositoryExtTest {

    @Autowired
    private UserRepositoryExt repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("When finding user by cpf")
    @Nested
    class FindByCpf {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a valid cpf, in scenario with register")
            @Test
            void test1() {
                //Scenario
                var customer = create_Customer()
                        .withAllFieldsExceptDB();
                customer.getAuthorities()
                        .forEach(authority -> em.persist(authority));
                repository.save(customer);
                em.flush();
                //Given
                var email = "john.doe@fiap.com.br";
                //When
                var actual = repository.findByEmail(email);
                //Then
                assertThat(actual)
                        .isPresent()
                        .get()
                        .extracting(User::getEmail)
                        .isEqualTo("john.doe@fiap.com.br");
            }
        }
    }
}