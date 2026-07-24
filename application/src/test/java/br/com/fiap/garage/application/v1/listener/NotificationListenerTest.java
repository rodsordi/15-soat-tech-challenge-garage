package br.com.fiap.garage.application.v1.listener;

import br.com.fiap.garage.domain.use_case.NotificationCreationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.fiap.garage.application.v1.msg.factory.NotificationMsgFactory.create_NotificationMsgFactory;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @InjectMocks
    NotificationListener listener;

    @Mock
    NotificationCreationUseCase notificationCreationUseCase;

    @DisplayName("When listening notification creation")
    @Nested
    class ListenNotificationCreation {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a NotificationMsg with all fields")
            @Test
            void test1() {
                //Given
                var msg = create_NotificationMsgFactory()
                        .withAllFields();
                //When
                listener.listenNotificationCreation(msg);
                //Then
                verify(notificationCreationUseCase, times(1))
                        .create(any());
            }
        }
    }
}