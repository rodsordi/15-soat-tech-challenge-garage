package br.com.fiap.garage.application.adapter.publisher;

import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.application.adapter.evt.NotificationEvt;
import br.com.fiap.garage.application.adapter.mapper.NotificationEvtMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
import static br.com.fiap.garage.domain.entity.factory.WorkOrderFactory.create_WorkOrder;
import static br.com.fiap.garage.application.adapter.evt.factory.NotificationEvtFactory.create_NotificationEvt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class NotifyCustomerForApprovalPublisherImplTest {

    @InjectMocks
    NotifyCustomerForApprovalPublisherImpl publisher;

    @Mock
    NotificationEvtMapper mapper;

    @Mock
    SnsTemplate snsTemplate;

    @BeforeEach
    void beforeEach() {
        setField(publisher, "queueName", "api-garage_notification-creation_topic");
    }

    @DisplayName("When notifying WorkOrder")
    @Nested
    class Notify {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(mapper.convert(any(WorkOrder.class)))
                        .thenReturn(create_NotificationEvt()
                                .withAllFields());
            }

            @DisplayName("Given a WorkOrder with all fields")
            @Test
            void test1() {
                //Given
                var workOrder = create_WorkOrder()
                        .withAllFields();
                setField(workOrder.getVehicle(), "customer", create_Customer()
                        .withAllFields());
                //When
                publisher.notify(workOrder);
                //Then
                verify(snsTemplate, times(1))
                        .convertAndSend(anyString(), any(NotificationEvt.class));
            }
        }
    }
}