package br.com.fiap.garage.application.v1.controller;

import br.com.fiap.commons.config.RestControllerTestConfig;
import br.com.fiap.commons.exception.BusinessException;
import br.com.fiap.garage.domain.entity.InventoryMaterial;
import br.com.fiap.garage.domain.use_case.InventoryMaterialCreationUseCase;
import br.com.fiap.garage.domain.use_case.InventoryMaterialSearchUseCase;
import br.com.fiap.garage.domain.use_case.InventoryMaterialUpdateUseCase;
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
import static br.com.fiap.garage.application.v1.dto.factory.InventoryMaterialDtoFactory.create_InventoryMaterialDto_Request;
import static br.com.fiap.garage.domain.entity.factory.InventoryMaterialFactory.create_InventoryMaterial;
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
@SpringBootTest(classes = InventoryMaterialController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = RestControllerTestConfig.class)
class InventoryMaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockitoBean
    private InventoryMaterialSearchUseCase inventoryMaterialSearchUseCase;

    @MockitoBean
    private InventoryMaterialCreationUseCase inventoryMaterialCreationUseCase;

    @MockitoBean
    private InventoryMaterialUpdateUseCase inventoryMaterialUpdateUseCase;

    @DisplayName("When creating InventoryMaterial")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(inventoryMaterialCreationUseCase.create(any()))
                        .thenAnswer(invocationOnMock -> {
                            InventoryMaterial inventoryMaterial = invocationOnMock.getArgument(0);
                            setField(inventoryMaterial, "id", fromString("7a403fc9-3c96-408c-984f-1fea2729b59f"));
                            return inventoryMaterial;
                        });
            }

            @DisplayName("Given a inventoryMaterial with all fields")
            @Test
            void test1() throws Exception {
                //Given
                var requestBody = create_InventoryMaterialDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(post("/v1/inventory-materials")
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

            @DisplayName("Given a inventoryMaterial with all fields, in scenario with BusinessException")
            @Test
            void test1() throws Exception {
                //Scenario
                when(inventoryMaterialCreationUseCase.create(any()))
                        .thenThrow(new BusinessException("Erro"));
                //Given
                var requestBody = create_InventoryMaterialDto_Request()
                        .withAllFields();
                //When
                mockMvc.perform(post("/v1/inventory-materials")
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON)
                                .characterEncoding(UTF_8.name())
                                .content(gson.toJson(requestBody)))
                        //Then
                        .andDo(print())
                        .andExpect(status().isUnprocessableContent())
                        .andExpect(jsonPath("$.detail", is("Erro")));
            }
        }
    }

    @DisplayName("When finding inventoryMaterial by id")
    @Nested
    class FindById {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(inventoryMaterialSearchUseCase.findById(any()))
                        .thenAnswer(invocationOnMock -> {
                            UUID inventoryMaterialId = invocationOnMock.getArgument(0);
                            var inventoryMaterial = create_InventoryMaterial().withAllFields();
                            setField(inventoryMaterial, "id", inventoryMaterialId);
                            return inventoryMaterial;
                        });
            }

            @DisplayName("Given a valid inventoryMaterialId")
            @Test
            void test1() throws Exception {
                //Given
                var inventoryMaterialId = "b17555de-3cb8-4ef5-8b43-6e3b3614d2f9";
                //When
                mockMvc.perform(get("/v1/inventory-materials/{0}", inventoryMaterialId))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is("b17555de-3cb8-4ef5-8b43-6e3b3614d2f9")))
                ;
            }
        }
    }
    
    @DisplayName("When finding all inventory materials")
    @Nested
    class FindAll {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(inventoryMaterialSearchUseCase.findAll(any()))
                        .thenAnswer(invocationOnMock -> {
                            var inventoryMaterials = List.of(
                                    create_InventoryMaterial().withAllFields(),
                                    create_InventoryMaterial().withAllFields(),
                                    create_InventoryMaterial().withAllFields());
                            return new PageImpl<>(inventoryMaterials);
                        });
            }

            @DisplayName("Given all filters")
            @Test
            void test1() throws Exception {
                //Scenario
                when(inventoryMaterialSearchUseCase.findAll(any()))
                        .thenAnswer(invocationOnMock -> {
                            assertThatObject(invocationOnMock.getArgument(0))
                                    .hasNoEmptyFields();
                            var inventoryMaterials = List.of(
                                    create_InventoryMaterial().withAllFields(),
                                    create_InventoryMaterial().withAllFields(),
                                    create_InventoryMaterial().withAllFields());
                            return new PageImpl<>(inventoryMaterials);
                        });
                //When
                mockMvc.perform(get("/v1/inventory-materials")
                                .queryParam("type", "SPARE_PART")
                                .queryParam("name", "John Doe")
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
            
            @DisplayName("Given empty filters")
            @Test
            void test2() throws Exception {
                //When
                mockMvc.perform(get("/v1/inventory-materials"))
                        //Then
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content.[*].id", hasSize(3)))
                ;
            }
        }
    }
}