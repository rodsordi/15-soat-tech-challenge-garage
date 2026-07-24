package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.commons.config.RestControllerTestConfig;
import br.com.fiap.garage.domain.entity.Vehicle;
import br.com.fiap.garage.domain.use_case.VehicleCreationUseCase;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static br.com.fiap.garage.application.v1.dto.factory.VehicleDtoFactory.create_VehicleDto_Request;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = NestedVehicleController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = RestControllerTestConfig.class)
class NestedVehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockitoBean
    private VehicleCreationUseCase vehicleCreationUseCase;

    @DisplayName("When creating Vehicle")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(vehicleCreationUseCase.create(any(), any()))
                        .thenAnswer(invocationOnMock -> {
                            Vehicle vehicle = invocationOnMock.getArgument(1);
                            setField(vehicle, "id", fromString("7a403fc9-3c96-408c-984f-1fea2729b59f"));
                            return vehicle;
                        });
            }

            @DisplayName("Given a valid customerId, a vehicle with all fields")
            @Test
            void test1() throws Exception {
                //Given
                var customerId = UUID.fromString("c957f04c-d766-4915-ab85-a50d0cef456d");
                var requestBody = create_VehicleDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(post(format("/v1/customers/{0}/vehicles", customerId))
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
}