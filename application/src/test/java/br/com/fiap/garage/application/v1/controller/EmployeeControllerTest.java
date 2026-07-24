package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.commons.config.RestControllerTestConfig;
import br.com.fiap.garage.domain.entity.Employee;
import br.com.fiap.garage.domain.use_case.EmployeeCreationUseCase;
import br.com.fiap.garage.domain.use_case.EmployeeSearchUseCase;
import br.com.fiap.garage.domain.use_case.EmployeeUpdateUseCase;
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
import static br.com.fiap.garage.application.v1.dto.factory.EmployeeDtoFactory.create_EmployeeDto_Request;
import static br.com.fiap.garage.domain.entity.factory.EmployeeFactory.create_Employee;
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
@SpringBootTest(classes = EmployeeController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = RestControllerTestConfig.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockitoBean
    private EmployeeCreationUseCase employeeCreationUseCase;

    @MockitoBean
    private EmployeeSearchUseCase employeeSearchUseCase;

    @MockitoBean
    private EmployeeUpdateUseCase employeeUpdateUseCase;

    @DisplayName("When creating Employee")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(employeeCreationUseCase.create(any()))
                        .thenAnswer(invocationOnMock -> {
                            Employee employee = invocationOnMock.getArgument(0);
                            setField(employee, "id", fromString("7a403fc9-3c96-408c-984f-1fea2729b59f"));
                            return employee;
                        });
            }

            @DisplayName("Given a employee with all fields")
            @Test
            void test1() throws Exception {
                //Given
                var requestBody = create_EmployeeDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(post("/v1/employees")
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

    @DisplayName("When finding employee by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(employeeSearchUseCase.findById(any()))
                        .thenAnswer(invocationOnMock -> {
                            UUID employeeId = invocationOnMock.getArgument(0);
                            var employee = create_Employee().withAllFields();
                            setField(employee, "id", employeeId);
                            return employee;
                        });
            }

            @DisplayName("Given a valid employeeId")
            @Test
            void test1() throws Exception {
                //Given
                var employeeId = "b17555de-3cb8-4ef5-8b43-6e3b3614d2f9";
                //When
                mockMvc.perform(get("/v1/employees/{0}", employeeId))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is("b17555de-3cb8-4ef5-8b43-6e3b3614d2f9")))
                ;
            }
        }
    }

    @DisplayName("When finding all employees")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(employeeSearchUseCase.findAll(any()))
                        .thenAnswer(invocationOnMock -> {
                            assertThatObject(invocationOnMock.getArgument(0))
                                    .hasNoEmptyFields();
                            var employees = List.of(
                                    create_Employee().withAllFields(),
                                    create_Employee().withAllFields(),
                                    create_Employee().withAllFields());
                            return new PageImpl<>(employees);
                        });
            }

            @DisplayName("Given all filters")
            @Test
            void test1() throws Exception {
                //When
                mockMvc.perform(get("/v1/employees")
                                .queryParam("cpf", "123.456.789-10")
                                .queryParam("name", "John")
                                .queryParam("email", "user@email.com")
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