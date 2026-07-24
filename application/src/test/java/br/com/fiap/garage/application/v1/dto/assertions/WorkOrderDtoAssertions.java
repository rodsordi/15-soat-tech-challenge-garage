package br.com.fiap.garage.application.v1.dto.assertions;

import br.com.fiap.garage.application.v1.dto.WorkOrderDto;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static br.com.fiap.commons.util.DateUtil.newDateTime;
import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static br.com.fiap.garage.application.v1.dto.assertions.EmployeeDtoAssertions.assertThat_EmployeeDto_Representation;
import static br.com.fiap.garage.application.v1.dto.assertions.EstimatedServiceDtoAssertions.assertThat_EstimatedServiceDto_Response;
import static br.com.fiap.garage.application.v1.dto.assertions.VehicleDtoAssertions.assertThat_VehicleDto_Representation;
import static br.com.fiap.garage.domain.enums.WorkOrderStatus.RECEIVED;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RequiredArgsConstructor(access = PRIVATE)
public final class WorkOrderDtoAssertions {

    public static Response assertThat_WorkOrderDto_Response(WorkOrderDto.Response actual) {
        assertThat(actual).isNotNull();
        return new Response(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Response {
        
        private final WorkOrderDto.Response actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.WorkOrderFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_WorkOrder() {
            // Self
            assertThat(actual.getId())
                    .hasToString("e48ad20c-69dd-4382-b567-0e02b2c3d480");
            assertThat(actual.getStatus())
                    .isEqualTo(RECEIVED);
            assertThat(actual.getTotalAmount())
                    .isEqualByComparingTo(new BigDecimal("999.99"));

            // Composition
            assertThat_VehicleDto_Representation(actual.getVehicle())
                    .wasConvertedFrom_Vehicle();
            assertThat_EmployeeDto_Representation(actual.getEmployee())
                    .wasConvertedFrom_Employee();
            assertThat_EstimatedServiceDto_Response(actual.getEstimatedServices().stream().findFirst().orElseThrow())
                    .wasConvertedFrom_EstimatedService();

            //Inheritance (AuditableTable)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));
            assertThat(actual.getUpdatedAt())
                    .isEqualTo(newDateTime("14/12/2026 23:59:59"));

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }

    public static Representation assertThat_WorkOrderDto_Representation(WorkOrderDto.Representation actual) {
        assertThat(actual).isNotNull();
        return new Representation(spy(actual));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Representation {

        private final WorkOrderDto.Representation actual;

        /**
         * @see br.com.fiap.garage.domain.entity.factory.WorkOrderFactory
         * .withAllFields()
         */
        public void wasConvertedFrom_WorkOrder() {
            // Self
            assertThat(actual.getId())
                    .hasToString("e48ad20c-69dd-4382-b567-0e02b2c3d480");
            assertThat(actual.getStatus())
                    .isEqualTo(RECEIVED);
            assertThat(actual.getTotalAmount())
                    .isEqualByComparingTo(new BigDecimal("999.99"));

            //Inheritance (AuditableTable)
            assertThat(actual.getCreatedAt())
                    .isEqualTo(newDateTime("13/12/2026 23:59:59"));
            assertThat(actual.getLinks())
                    .hasToString("</v1/work-orders/e48ad20c-69dd-4382-b567-0e02b2c3d480>;rel=\"self\"");

            // And
            assertThatObject(actual)
                    .hasAllGetMethodsVerifiedOnceAtLeast();
        }
    }
}