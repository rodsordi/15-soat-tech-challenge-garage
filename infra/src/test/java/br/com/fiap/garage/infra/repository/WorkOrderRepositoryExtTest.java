package br.com.fiap.garage.infra.repository;

import br.com.fiap.commons.config.JpaConfig;
import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.enums.WorkOrderStatus;
import br.com.fiap.garage.domain.filter.WorkOrderFilter;
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
import static br.com.fiap.garage.domain.entity.factory.VehicleFactory.create_Vehicle;
import static br.com.fiap.garage.domain.entity.factory.WorkOrderFactory.create_WorkOrder;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.*;
import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = JpaConfig.class)
class WorkOrderRepositoryExtTest {

    @Autowired
    private WorkOrderRepositoryExt repository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("When finding all workOrders")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a filter with all fields, in scenario with registers")
            @Test
            void test1() {
                //Scenario
                var workOrder = create_WorkOrder()
                        .withAllFieldsExceptDB();
                workOrder = em.merge(workOrder);
                em.flush();
                setField(workOrder, "createdAt", newDateTime("13/12/2026 23:59:59"));
                em.flush();
                //Given
                var filter = new WorkOrderFilter();
                filter.setStatus(RECEIVED);
                filter.setCreatedAtFrom(newDate("13/12/2026"));
                filter.setCreatedAtTo(newDate("13/12/2026"));
                assertThatObject(filter)
                        .hasNoEmptyFields();
                //When
                var actual = repository.findAll(filter, filter.buildPageRequest());
                //Then
                assertThat(actual)
                        .hasSize(1)
                        .extracting(WorkOrder::getStatus)
                        .containsExactly(RECEIVED);
            }

            @DisplayName("Given no filter, in scenario with lots of registers with different statuses")
            @Test
            void test2() {
                //Scenario
                var employee = create_Employee()
                        .withAllFieldsExceptDB();
                employee = em.merge(employee);
                em.flush();
                //And
                var vehicle = create_Vehicle()
                        .withAllFieldsExceptDB();
                vehicle = em.merge(vehicle);
                em.flush();
                //And
                for (var status : WorkOrderStatus.values()) {
                    for (var i = 1; i <= 3; i++) {
                        var workOrder = create_WorkOrder()
                                .withAllFieldsExceptDB();
                        setField(workOrder, "status", status);
                        setField(workOrder, "vehicle", vehicle);
                        setField(workOrder, "employee", employee);
                        workOrder = em.merge(workOrder);
                        em.flush();
                        //And
                        setField(workOrder, "createdAt", newDateTime(format("0{0}/12/2026 23:59:59", i)));
                        em.flush();
                    }
                }

                //Given
                var filter = new WorkOrderFilter();
                //When
                var actual = repository.findAll(filter, filter.buildPageRequest());
                //Then
                assertThat(actual)
                        .hasSize(12)
                        .extracting(WorkOrder::getStatus, WorkOrder::getCreatedAt)
                        .containsExactly(
                                tuple(EXECUTING, newDateTime("03/12/2026 23:59:59")),
                                tuple(EXECUTING, newDateTime("02/12/2026 23:59:59")),
                                tuple(EXECUTING, newDateTime("01/12/2026 23:59:59")),
                                tuple(WAITING_FOR_APPROVAL, newDateTime("03/12/2026 23:59:59")),
                                tuple(WAITING_FOR_APPROVAL, newDateTime("02/12/2026 23:59:59")),
                                tuple(WAITING_FOR_APPROVAL, newDateTime("01/12/2026 23:59:59")),
                                tuple(DIAGNOSING, newDateTime("03/12/2026 23:59:59")),
                                tuple(DIAGNOSING, newDateTime("02/12/2026 23:59:59")),
                                tuple(DIAGNOSING, newDateTime("01/12/2026 23:59:59")),
                                tuple(RECEIVED, newDateTime("03/12/2026 23:59:59")),
                                tuple(RECEIVED, newDateTime("02/12/2026 23:59:59")),
                                tuple(RECEIVED, newDateTime("01/12/2026 23:59:59")));
            }
        }
    }
}