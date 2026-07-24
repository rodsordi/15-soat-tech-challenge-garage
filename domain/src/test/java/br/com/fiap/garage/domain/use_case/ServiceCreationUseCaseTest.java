package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
import br.com.fiap.garage.domain.entity.Service;
import br.com.fiap.garage.domain.repository.MaterialRepository;
import br.com.fiap.garage.domain.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static br.com.fiap.garage.domain.entity.factory.MaterialFactory.create_Material;
import static br.com.fiap.garage.domain.entity.factory.ServiceFactory.create_Service;
import static java.util.UUID.fromString;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class ServiceCreationUseCaseTest {

    @InjectMocks
    private ServiceCreationUseCase serviceCreationUseCase;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private MaterialRepository materialRepository;

    @DisplayName("When creating Service")
    @Nested
    class Create {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(serviceRepository.save(any()))
                        .thenAnswer(invocationOnMock -> {
                            Service service = invocationOnMock.getArgument(0);
                            setField(service, "id", fromString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577"));
                            return service;
                        });

                when(materialRepository.findById(any()))
                        .thenReturn(Optional.of(create_Material()
                                .withAllFields()));
            }

            @DisplayName("Given a Service with all fields, and valid materialsIds")
            @Test
            void test1() {
                //Given
                var service = create_Service()
                        .withAllFields();
                var materialsIds = Set.of(UUID.fromString("0b8ecbbb-d51a-41ed-ba1d-8d33a629509a"));
                //When
                var actual = serviceCreationUseCase.create(service, materialsIds);
                //Then
                assertThat(actual.getId())
                        .hasToString("c0a1f176-d3e6-4910-8fba-9a6c31bc5577");
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a Service with all fields, and valid materialsIds, in scenario with no registered materials")
            @Test
            void test1() {
                //Given
                var service = create_Service()
                        .withAllFields();
                var materialsIds = Set.of(UUID.fromString("0b8ecbbb-d51a-41ed-ba1d-8d33a629509a"));
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> serviceCreationUseCase.create(service, materialsIds));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Material] with [id]: [0b8ecbbb-d51a-41ed-ba1d-8d33a629509a] not found");
            }
        }
    }
}