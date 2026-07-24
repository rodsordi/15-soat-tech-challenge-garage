package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.commons.config.RestControllerTestConfig;
import br.com.fiap.garage.domain.entity.Notification;
import br.com.fiap.garage.domain.use_case.NotificationCreationUseCase;
import br.com.fiap.garage.domain.use_case.NotificationSearchUseCase;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.factory.NotificationDtoFactory.create_NotificationDto_Request;
import static br.com.fiap.garage.domain.entity.factory.NotificationFactory.create_Notification;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = NotificationController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = RestControllerTestConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockitoBean
    private NotificationCreationUseCase notificationCreationUseCase;

    @MockitoBean
    private NotificationSearchUseCase notificationSearchUseCase;

    @DisplayName("When creating Notification")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(notificationCreationUseCase.create(any()))
                        .thenAnswer(invocationOnMock -> {
                            Notification notification = invocationOnMock.getArgument(0);
                            setField(notification, "id", fromString("7a403fc9-3c96-408c-984f-1fea2729b59f"));
                            return notification;
                        });
            }

            @DisplayName("Given a notification with all fields")
            @Test
            void test1() throws Exception {
                //Given
                var requestBody = create_NotificationDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(post("/v1/notifications")
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON)
                                .characterEncoding(UTF_8.name())
                                .content(gson.toJson(requestBody)))
                        //Then
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id", is("7a403fc9-3c96-408c-984f-1fea2729b59f")));
            }
        }
    }

    @DisplayName("When finding notification by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(notificationSearchUseCase.findById(any()))
                        .thenAnswer(invocationOnMock -> {
                            UUID notificationId = invocationOnMock.getArgument(0);
                            var notification = create_Notification().withAllFields();
                            setField(notification, "id", notificationId);
                            return notification;
                        });
            }

            @DisplayName("Given a valid notificationId")
            @Test
            void test1() throws Exception {
                //Given
                var notificationId = "b17555de-3cb8-4ef5-8b43-6e3b3614d2f9";
                //When
                mockMvc.perform(get("/v1/notifications/{0}", notificationId))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is("b17555de-3cb8-4ef5-8b43-6e3b3614d2f9")))
                ;
            }
        }
    }

    @DisplayName("When finding all notifications")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(notificationSearchUseCase.findAll(any()))
                        .thenAnswer(invocationOnMock -> {
                            assertThatObject(invocationOnMock.getArgument(0))
                                    .hasNoEmptyFields();
                            var notifications = List.of(
                                    create_Notification().withAllFields(),
                                    create_Notification().withAllFields(),
                                    create_Notification().withAllFields());
                            return new PageImpl<>(notifications);
                        });
            }

            @DisplayName("Given all filters")
            @Test
            void test1() throws Exception {
                //When
                mockMvc.perform(get("/v1/notifications")
                                .queryParam("externalId", "c273c76e-6f8b-4ca6-97a0-fe88f29cb523")
                                .queryParam("recipient", "john.doe@email.com")
                                .queryParam("createdAtFrom", "2025-01-01")
                                .queryParam("createdAtTo", "2025-12-31")
                                .queryParam("updatedAtFrom", "2025-01-01")
                                .queryParam("updatedAtTo", "2025-12-31"))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.[*].id", hasSize(3)))
                ;
            }
        }
    }
}