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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
import static br.com.fiap.garage.domain.entity.factory.WorkOrderFactory.create_WorkOrder;
import static br.com.fiap.garage.application.adapter.evt.factory.NotificationEvtFactory.create_NotificationEvt;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class NotifyCustomerForApprovalPublisherImplTest {

    private NotifyCustomerForApprovalPublisherImpl publisher;

    @Mock
    private NotificationEvtMapper mapper;

    @Mock
    private SnsTemplate snsTemplate;

    private static final String TOPIC_NAME = "api-garage_notification-creation_topic";

    @BeforeEach
    void setUp() {
        publisher = new NotifyCustomerForApprovalPublisherImpl(mapper, Optional.of(snsTemplate));
        setField(publisher, "topicName", TOPIC_NAME);
    }

    @DisplayName("When notifying WorkOrder")
    @Nested
    class Notify {

        @DisplayName("Then should execute successfully when SNS is available")
        @Nested
        class Success {

            @BeforeEach
            void setUp() {
                when(mapper.convert(any(WorkOrder.class)))
                        .thenReturn(create_NotificationEvt().withAllFields());
            }

            @DisplayName("Given a WorkOrder with all fields")
            @Test
            void shouldPublishSuccessfully() {
                // Given
                var workOrder = create_WorkOrder().withAllFields();
                setField(workOrder.getVehicle(), "customer", create_Customer().withAllFields());

                // When
                publisher.notify(workOrder);

                // Then
                verify(mapper, times(1)).convert(workOrder);
                verify(snsTemplate, times(1)).convertAndSend(eq(TOPIC_NAME), any(NotificationEvt.class));
            }
        }

        @DisplayName("Then should handle safely when SNS is disabled or absent")
        @Nested
        class WhenSnsIsAbsent {

            @DisplayName("Given SnsTemplate is Optional.empty()")
            @Test
            void shouldSkipNotificationGracefully() {
                // Given
                var publisherWithoutSns = new NotifyCustomerForApprovalPublisherImpl(mapper, Optional.empty());
                setField(publisherWithoutSns, "topicName", TOPIC_NAME);
                var workOrder = create_WorkOrder().withAllFields();

                // When / Then
                assertDoesNotThrow(() -> publisherWithoutSns.notify(workOrder));
                verifyNoInteractions(mapper);
                verifyNoInteractions(snsTemplate);
            }
        }

        @DisplayName("Then should not throw exception when SNS fails")
        @Nested
        class WhenSnsFails {

            @BeforeEach
            void setUp() {
                when(mapper.convert(any(WorkOrder.class)))
                        .thenReturn(create_NotificationEvt().withAllFields());
                doThrow(new RuntimeException("Unable to load credentials or broker unreachable"))
                        .when(snsTemplate).convertAndSend(anyString(), any(NotificationEvt.class));
            }

            @DisplayName("Given SnsTemplate throws an exception")
            @Test
            void shouldCatchAndLogWithoutThrowing() {
                // Given
                var workOrder = create_WorkOrder().withAllFields();
                setField(workOrder.getVehicle(), "customer", create_Customer().withAllFields());

                // When / Then
                assertDoesNotThrow(() -> publisher.notify(workOrder));
                verify(mapper, times(1)).convert(workOrder);
                verify(snsTemplate, times(1)).convertAndSend(eq(TOPIC_NAME), any(NotificationEvt.class));
            }
        }
    }
}