package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.commons.config.RestControllerTestConfig;
import br.com.fiap.commons.exception.BusinessException;
import br.com.fiap.garage.domain.entity.Customer;
import br.com.fiap.garage.domain.use_case.CustomerCreationUseCase;
import br.com.fiap.garage.domain.use_case.CustomerSearchUseCase;
import br.com.fiap.garage.domain.use_case.CustomerUpdateUseCase;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import static br.com.fiap.garage.application.v1.dto.factory.CustomerDtoFactory.create_CustomerDto_Request;
import static br.com.fiap.garage.domain.entity.factory.CustomerFactory.create_Customer;
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
@SpringBootTest(classes = CustomerController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = RestControllerTestConfig.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockitoBean
    private CustomerCreationUseCase customerCreationUseCase;

    @MockitoBean
    private CustomerSearchUseCase customerSearchUseCase;

    @MockitoBean
    private CustomerUpdateUseCase customerUpdateUseCase;

    @DisplayName("When creating Customer")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(customerCreationUseCase.create(any()))
                        .thenAnswer(invocationOnMock -> {
                            Customer customer = invocationOnMock.getArgument(0);
                            setField(customer, "id", fromString("7a403fc9-3c96-408c-984f-1fea2729b59f"));
                            return customer;
                        });
            }

            @DisplayName("Given a customer with all fields")
            @Test
            void test1() throws Exception {
                //Given
                var requestBody = create_CustomerDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(post("/v1/customers")
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

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a customer with all fields, in scenario with BusinessException")
            @Test
            void test1() throws Exception {
                //Scenario
                when(customerCreationUseCase.create(any()))
                        .thenThrow(new BusinessException("Erro"));
                //Given
                var requestBody = create_CustomerDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(post("/v1/customers")
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON)
                                .characterEncoding(UTF_8.name())
                                .content(gson.toJson(requestBody)))
                        //Then
                        .andDo(print())
                        .andExpect(status().isUnprocessableContent())
                        .andExpect(jsonPath("$.detail", is("Erro")));
            }

            @DisplayName("Given a customer with invalid document")
            @ParameterizedTest
            @CsvSource(value = {
                    "12345678911 | [document]: '12345678911' is invalid. Reason: Document is not a valid CPF neither CNPJ.",
                    "10123456000190 | [document]: '10123456000190' is invalid. Reason: Document is not a valid CPF neither CNPJ.",
            }, delimiterString = "|")
            void test(String document, String expectedMsg) throws Exception {
                //Given
                var requestBody = create_CustomerDto_Request()
                        .withAllFields();
                setField(requestBody, "document", document);
                //When
                mockMvc.perform(post("/v1/customers")
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON)
                                .characterEncoding(UTF_8.name())
                                .content(gson.toJson(requestBody)))
                        //Then
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.detail", is(expectedMsg)));
            }
        }
    }

    @DisplayName("When finding customer by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(customerSearchUseCase.findById(any()))
                        .thenAnswer(invocationOnMock -> {
                            UUID customerId = invocationOnMock.getArgument(0);
                            var customer = create_Customer().withAllFields();
                            setField(customer, "id", customerId);
                            return customer;
                        });
            }

            @DisplayName("Given a valid customerId")
            @Test
            void test1() throws Exception {
                //Given
                var customerId = "b17555de-3cb8-4ef5-8b43-6e3b3614d2f9";
                //When
                mockMvc.perform(get("/v1/customers/{0}", customerId))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is("b17555de-3cb8-4ef5-8b43-6e3b3614d2f9")))
                ;
            }
        }
    }

    @DisplayName("When finding all customers")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(customerSearchUseCase.findAll(any()))
                        .thenAnswer(invocationOnMock -> {
                            assertThatObject(invocationOnMock.getArgument(0))
                                    .hasNoEmptyFields();
                            var customers = List.of(
                                    create_Customer().withAllFields(),
                                    create_Customer().withAllFields(),
                                    create_Customer().withAllFields());
                            return new PageImpl<>(customers);
                        });
            }

            @DisplayName("Given all filters")
            @Test
            void test1() throws Exception {
                //When
                mockMvc.perform(get("/v1/customers")
                                .queryParam("document", "27351626000107")
                                .queryParam("name", "John Doe")
                                .queryParam("email", "john.doe@fiap.com.br")
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