package br.com.fiap.garage.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.garage.domain.entity.factory.EstimatedServiceFactory.create_EstimatedService;
import static org.assertj.core.api.Assertions.assertThat;

class EstimatedServiceTest {

    private EstimatedService estimatedService;

    @DisplayName("When finishing EstimatedService")
    @Nested
    class Finish {

        @DisplayName("Then should execute successfully")
        @Nested
        class Success {

            @DisplayName("Given a initiated empty EstimatedService")
            @Test
            void test1() {
                //Given
                estimatedService = create_EstimatedService().initiatedEmpty();
                //When
                estimatedService.finish();
                //Then
                assertThat(estimatedService.getFinishedAt()).isNotNull();
            }
        }
    }
}