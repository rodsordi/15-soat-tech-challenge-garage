package br.com.fiap.garage.application.adapter.repository;

import br.com.fiap.commons.config.JpaConfig;
import br.com.fiap.garage.domain.entity.Notification;
import br.com.fiap.garage.domain.filter.NotificationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static br.com.fiap.commons.util.DateUtil.newDate;
import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.domain.entity.factory.NotificationFactory.create_Notification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = JpaConfig.class)
class NotificationRepositoryExtTest {

    @Autowired
    private NotificationRepositoryExt repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("When finding all notifications")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a filter with all fields, in scenario with registers")
            @Test
            void test1() {
                //Scenario
                var notification = create_Notification()
                        .withAllFieldsExceptDB();
                notification = em.merge(notification);
                em.flush();
                setField(notification,  "createdAt", newDateTime("13/12/2026 23:59:59"));
                em.flush();
                //Given
                var filter = new NotificationFilter();
                filter.setExternalId(UUID.fromString("d2b16521-39ce-479c-b779-a9ed5238a6c3"));
                filter.setRecipient("customer@example.com");
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
                                Notification::getExternalId,
                                n -> n.getEmail().getRecipient())
                        .containsExactly(tuple(
                                UUID.fromString("d2b16521-39ce-479c-b779-a9ed5238a6c3"),
                                "customer@example.com"));
            }
        }
    }
}