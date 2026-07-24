package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.commons.config.RestControllerTestConfig;
import br.com.fiap.garage.domain.use_case.VehicleCreationUseCase;
import br.com.fiap.garage.domain.use_case.VehicleSearchUseCase;
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
import static br.com.fiap.garage.domain.entity.factory.VehicleFactory.create_Vehicle;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = VehicleController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = RestControllerTestConfig.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockitoBean
    private VehicleCreationUseCase vehicleCreationUseCase;

    @MockitoBean
    private VehicleSearchUseCase vehicleSearchUseCase;

    @DisplayName("When finding vehicle by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(vehicleSearchUseCase.findById(any()))
                        .thenAnswer(invocationOnMock -> {
                            UUID vehicleId = invocationOnMock.getArgument(0);
                            var vehicle = create_Vehicle().withAllFields();
                            setField(vehicle, "id", vehicleId);
                            return vehicle;
                        });
            }

            @DisplayName("Given a valid vehicleId")
            @Test
            void test1() throws Exception {
                //Given
                var vehicleId = "b17555de-3cb8-4ef5-8b43-6e3b3614d2f9";
                //When
                mockMvc.perform(get("/v1/vehicles/{0}", vehicleId))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is("b17555de-3cb8-4ef5-8b43-6e3b3614d2f9")))
                ;
            }
        }
    }

    @DisplayName("When finding all vehicles")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(vehicleSearchUseCase.findAll(any()))
                        .thenAnswer(invocationOnMock -> {
                            assertThatObject(invocationOnMock.getArgument(0))
                                    .hasNoEmptyFields();
                            var vehicles = List.of(
                                    create_Vehicle().withAllFields(),
                                    create_Vehicle().withAllFields(),
                                    create_Vehicle().withAllFields());
                            return new PageImpl<>(vehicles);
                        });
            }

            @DisplayName("Given all filters")
            @Test
            void test1() throws Exception {
                //When
                mockMvc.perform(get("/v1/vehicles")
                                .queryParam("make", "Volkswagen")
                                .queryParam("model", "Gol")
                                .queryParam("licensePlate", "ABC1C34")
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