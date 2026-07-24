package br.com.fiap.garage.application.v1.dto.factory;

import br.com.fiap.garage.application.v1.dto.WorkOrderDto;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static br.com.fiap.commons.util.ReflectionUtil.assertThatObject;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class WorkOrderDtoFactory {

    public static Request create_WorkOrderDto_Request() {
        return new Request(WorkOrderDto.Request.builder());
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static final class Request {

        private final WorkOrderDto.Request.RequestBuilder builder;

        public WorkOrderDto.Request withAllFields() {
            var result = builder
                    .vehicleId(UUID.fromString("5b3b7f42-0a9f-4093-82af-a7db99131e7c"))
                    .employeeId(UUID.fromString("22e9c194-562a-4029-9bc1-ed37e0a80966"))
                    .serviceId(UUID.fromString("0913e18b-84bd-4619-ad0a-c77600960346"))
                    .serviceId(UUID.fromString("f8b0855c-7fd5-4f50-b762-73e947328339"))
                    .serviceId(UUID.fromString("95919c3c-f8c3-4324-bddf-196198a273a5"))
                    .build();

            assertThatObject(result).hasNoEmptyFields();
            return result;
        }

        public WorkOrderDto.Request valid() {
            return builder
                    .vehicleId(UUID.fromString("5b3b7f42-0a9f-4093-82af-a7db99131e7c"))
                    .employeeId(UUID.fromString("22e9c194-562a-4029-9bc1-ed37e0a80966"))
                    .serviceId(UUID.fromString("0913e18b-84bd-4619-ad0a-c77600960346"))
                    .serviceId(UUID.fromString("f8b0855c-7fd5-4f50-b762-73e947328339"))
                    .serviceId(UUID.fromString("95919c3c-f8c3-4324-bddf-196198a273a5"))
                    .build();
        }

        public WorkOrderDto.Request initiatedEmpty() {
            return builder
                    .build();
        }
    }
}