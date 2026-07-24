package br.com.fiap.garage.domain.use_case;

import br.com.fiap.commons.exception.ResourceNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceUpdateUseCaseTest {

    @InjectMocks
    private ServiceUpdateUseCase serviceUpdateUseCase;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private MaterialRepository materialRepository;

    @DisplayName("When updating Service")
    @Nested
    class Update {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @BeforeEach
            void beforeEach() {
                when(serviceRepository.findById(any()))
                        .thenReturn(Optional.of(create_Service()
                                .withAllFields()));

                when(serviceRepository.save(any()))
                        .thenAnswer(i -> i.getArgument(0));

                when(materialRepository.findById(any()))
                        .thenReturn(Optional.of(create_Material()
                                .withAllFields()));
            }

            @DisplayName("Given a valid id, and valid materialsIds, and a Service with all fields")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var materialsIds = Set.of(UUID.fromString("bd0eff0f-5165-4995-8e12-ad065d829fa8"));
                var service = create_Service()
                        .withAllFields();
                //When
                var actual = serviceUpdateUseCase.update(id, materialsIds, service);
                //Then
                assertThat(actual).isNotNull();
                verify(serviceRepository, times(1)).save(any());
            }
        }

        @DisplayName("Then should return error")
        @Nested
        class Failure {

            @DisplayName("Given a Service with all fields, in scenario with no registered Service")
            @Test
            void test1() {
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var materialsIds = Set.of(UUID.fromString("bd0eff0f-5165-4995-8e12-ad065d829fa8"));
                var service = create_Service()
                        .withAllFields();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> serviceUpdateUseCase.update(id, materialsIds, service));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Service] with [id]: [b06b216f-215d-41eb-8c03-570f03562064] not found");
            }

            @DisplayName("Given a Service with all fields, in scenario with no registered Material")
            @Test
            void test2() {
                //Scenario
                when(serviceRepository.findById(any()))
                        .thenReturn(Optional.of(create_Service()
                                .withAllFields()));
                //Given
                var id = UUID.fromString("b06b216f-215d-41eb-8c03-570f03562064");
                var materialsIds = Set.of(UUID.fromString("bd0eff0f-5165-4995-8e12-ad065d829fa8"));
                var service = create_Service()
                        .withAllFields();
                //When
                var actual = assertThrows(ResourceNotFoundException.class,
                        () -> serviceUpdateUseCase.update(id, materialsIds, service));
                //Then
                assertThat(actual)
                        .hasMessage("Resource [Material] with [id]: [bd0eff0f-5165-4995-8e12-ad065d829fa8] not found");
            }
        }
    }
}