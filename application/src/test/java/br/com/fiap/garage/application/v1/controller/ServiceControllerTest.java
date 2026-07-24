package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.commons.config.RestControllerTestConfig;
import br.com.fiap.garage.domain.entity.Service;
import br.com.fiap.garage.domain.use_case.ServiceCreationUseCase;
import br.com.fiap.garage.domain.use_case.ServiceSearchUseCase;
import br.com.fiap.garage.domain.use_case.ServiceUpdateUseCase;
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
import static br.com.fiap.garage.application.v1.dto.factory.ServiceDtoFactory.create_ServiceDto_Request;
import static br.com.fiap.garage.domain.entity.factory.ServiceFactory.create_Service;
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
@SpringBootTest(classes = ServiceController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = RestControllerTestConfig.class)
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockitoBean
    private ServiceCreationUseCase serviceCreationUseCase;

    @MockitoBean
    private ServiceSearchUseCase serviceSearchUseCase;

    @MockitoBean
    private ServiceUpdateUseCase serviceUpdateUseCase;

    @DisplayName("When creating Service")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(serviceCreationUseCase.create(any(), any()))
                        .thenAnswer(invocationOnMock -> {
                            Service service = invocationOnMock.getArgument(0);
                            setField(service, "id", fromString("7a403fc9-3c96-408c-984f-1fea2729b59f"));
                            return service;
                        });
            }

            @DisplayName("Given a service with all fields")
            @Test
            void test1() throws Exception {
                //Given
                var requestBody = create_ServiceDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(post("/v1/services")
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

    @DisplayName("When finding service by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(serviceSearchUseCase.findById(any()))
                        .thenAnswer(invocationOnMock -> {
                            UUID serviceId = invocationOnMock.getArgument(0);
                            var service = create_Service().withAllFields();
                            setField(service, "id", serviceId);
                            return service;
                        });
            }

            @DisplayName("Given a valid serviceId")
            @Test
            void test1() throws Exception {
                //Given
                var serviceId = "b17555de-3cb8-4ef5-8b43-6e3b3614d2f9";
                //When
                mockMvc.perform(get("/v1/services/{0}", serviceId))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is("b17555de-3cb8-4ef5-8b43-6e3b3614d2f9")))
                ;
            }
        }
    }

    @DisplayName("When finding all services")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(serviceSearchUseCase.findAll(any()))
                        .thenAnswer(invocationOnMock -> {
                            assertThatObject(invocationOnMock.getArgument(0))
                                    .hasNoEmptyFields();
                            var services = List.of(
                                    create_Service().withAllFields(),
                                    create_Service().withAllFields(),
                                    create_Service().withAllFields());
                            return new PageImpl<>(services);
                        });
            }

            @DisplayName("Given all filters")
            @Test
            void test1() throws Exception {
                //When
                mockMvc.perform(get("/v1/services")
                                .queryParam("name", "Oil")
                                .queryParam("costFrom", "1.99")
                                .queryParam("costTo", "9.99")
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

    @DisplayName("When updating Service")
    @Nested
    class Update {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(serviceUpdateUseCase.update(any(), any(), any()))
                        .thenAnswer(invocationOnMock -> {
                            Service service = invocationOnMock.getArgument(2);
                            setField(service, "id", fromString("7a403fc9-3c96-408c-984f-1fea2729b59f"));
                            return service;
                        });
            }

            @DisplayName("Given a service with all fields")
            @Test
            void test1() throws Exception {
                //Given
                var requestBody = create_ServiceDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(put("/v1/services/{serviceId}", "7a403fc9-3c96-408c-984f-1fea2729b59f")
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON)
                                .characterEncoding(UTF_8.name())
                                .content(gson.toJson(requestBody)))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is("7a403fc9-3c96-408c-984f-1fea2729b59f")));
            }
        }
    }
}