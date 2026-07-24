package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.commons.config.RestControllerTestConfig;
import br.com.fiap.garage.domain.entity.WorkOrder;
import br.com.fiap.garage.domain.use_case.WorkOrderCreationUseCase;
import br.com.fiap.garage.domain.use_case.WorkOrderSearchUseCase;
import br.com.fiap.garage.domain.use_case.WorkOrderUpdateUseCase;
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
import static br.com.fiap.garage.application.v1.dto.factory.WorkOrderDtoFactory.create_WorkOrderDto_Request;
import static br.com.fiap.garage.domain.entity.factory.WorkOrderFactory.create_WorkOrder;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = WorkOrderController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = RestControllerTestConfig.class)
class WorkOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockitoBean
    private WorkOrderCreationUseCase workOrderCreationUseCase;

    @MockitoBean
    private WorkOrderSearchUseCase workOrderSearchUseCase;

    @MockitoBean
    private WorkOrderUpdateUseCase workOrderUpdateUseCase;

    @DisplayName("When creating WorkOrder")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(workOrderCreationUseCase.create(any(), any()))
                        .thenAnswer(invocationOnMock -> {
                            WorkOrder workOrder = invocationOnMock.getArgument(0);
                            setField(workOrder, "id", fromString("7a403fc9-3c96-408c-984f-1fea2729b59f"));
                            return workOrder;
                        });
            }

            @DisplayName("Given a workOrder with all fields")
            @Test
            void test1() throws Exception {
                //Given
                var requestBody = create_WorkOrderDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(post("/v1/work-orders")
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

    @DisplayName("When finding workOrder by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(workOrderSearchUseCase.findById(any()))
                        .thenAnswer(invocationOnMock -> {
                            UUID workOrderId = invocationOnMock.getArgument(0);
                            var workOrder = create_WorkOrder().withAllFields();
                            setField(workOrder, "id", workOrderId);
                            return workOrder;
                        });
            }

            @DisplayName("Given a valid workOrderId")
            @Test
            void test1() throws Exception {
                //Given
                var workOrderId = "b17555de-3cb8-4ef5-8b43-6e3b3614d2f9";
                //When
                mockMvc.perform(get("/v1/work-orders/{0}", workOrderId))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is("b17555de-3cb8-4ef5-8b43-6e3b3614d2f9")))
                ;
            }
        }
    }

    @DisplayName("When finding all workOrders")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(workOrderSearchUseCase.findAll(any()))
                        .thenAnswer(invocationOnMock -> {
                            assertThatObject(invocationOnMock.getArgument(0))
                                    .hasNoEmptyFields();
                            var workOrders = List.of(
                                    create_WorkOrder().withAllFields(),
                                    create_WorkOrder().withAllFields(),
                                    create_WorkOrder().withAllFields());
                            return new PageImpl<>(workOrders);
                        });
            }

            @DisplayName("Given all filters")
            @Test
            void test1() throws Exception {
                //When
                mockMvc.perform(get("/v1/work-orders")
                                .queryParam("status", "RECEIVED")
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

    @DisplayName("When updating workOrder")
    @Nested
    class Update {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(workOrderUpdateUseCase.finishService(any(), any()))
                        .thenAnswer(invocation -> {
                            var result = create_WorkOrder().withAllFields();
                            setField(result, "id", invocation.getArgument(0));
                            return result;
                        });
                //And
                when(workOrderUpdateUseCase.updateEmployee(any(), any()))
                        .thenAnswer(invocation -> {
                            var result = create_WorkOrder().withAllFields();
                            setField(result, "id", invocation.getArgument(0));
                            return result;
                        });
                //And
                when(workOrderUpdateUseCase.updateStatus(any(), any()))
                        .thenAnswer(invocation -> {
                            var result = create_WorkOrder().withAllFields();
                            setField(result, "id", invocation.getArgument(0));
                            return result;
                        });
            }

            @DisplayName("Given a WorkOrderDto with all fields and a valid workOrderId")
            @Test
            void test1() throws Exception {
                //Given
                var requestBody = create_WorkOrderDto_Request()
                        .withAllFields();
                var workOrderId = "b17555de-3cb8-4ef5-8b43-6e3b3614d2f9";
                //When
                mockMvc.perform(patch("/v1/work-orders/{0}", workOrderId)
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON)
                                .characterEncoding(UTF_8.name())
                                .content(gson.toJson(requestBody)))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is("b17555de-3cb8-4ef5-8b43-6e3b3614d2f9")))
                ;
            }
        }
    }
}