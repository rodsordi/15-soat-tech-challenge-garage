package br.com.fiap.garage.application.adapter.mapper;

import br.com.fiap.garage.domain.entity.WorkOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
import static br.com.fiap.garage.domain.entity.factory.WorkOrderFactory.create_WorkOrder;
import static br.com.fiap.garage.application.adapter.evt.assertions.NotificationEvtAssertions.assertThat_NotificationEvt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class NotificationEvtMapperTest {

    private static final NotificationEvtMapper mapper = new NotificationEvtMapper();

    @BeforeEach
    void beforeEach() {
        setField(mapper, "garageWebPageUrl", "http://localhost:8080/garage");
        setField(mapper, "estimateCustomerApprovalEmailSubject", "Email subject");
        setField(mapper, "emailBodyTemplateFileName", "/estimate-customer-approval-email-message.html");
    }

    @DisplayName("When convert NotificationEvt to Service")
    @Nested
    class Convert {

        @DisplayName("Then must execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a NotificationEvt with all fields")
            @Test
            void test1() {
                //Given
                var source = create_WorkOrder()
                        .withAllFields();
                var vehicle = source.getVehicle();
                setField(vehicle, "customer", create_Customer().withAllFields());
                //When
                var actual = mapper.convert(source);
                //Then
                assertThat_NotificationEvt(actual)
                        .wasConvertedFrom_WorkOrder();
            }

            @DisplayName("Given a null NotificationEvt")
            @Test
            void test2() {
                //Given
                WorkOrder source = null;
                //When
                var actual = mapper.convert(source);
                //Then
                assertThat(actual)
                        .isNull();
            }
        }
    }
}