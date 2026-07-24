package br.com.fiap.garage.application.v1.dto;

import br.com.fiap.garage.application.v1.def.EmployeeDef;
import br.com.fiap.garage.application.v1.mapper.EmployeeDtoMapper;
import br.com.fiap.garage.domain.entity.Employee;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@NoArgsConstructor(access = PRIVATE)
public final class EmployeeDto {
    
    private static final EmployeeDtoMapper MAPPER = getMapper(EmployeeDtoMapper.class);

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Employee.Request")
    public static class Request implements EmployeeDef.Request {
        private String username;
        private String password;
        private String name;
        private String email;
        private String cpf;

        public Employee buildEmployee() {
            return MAPPER.convert(this);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Employee.Response")
    public static class Response implements EmployeeDef.Response {
        private UUID id;
        private String username;
        private String name;
        private String email;
        private String cpf;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public static EmployeeDto.Response buildEmployeeDtoResponse(Employee employee) {
            return MAPPER.convert(employee);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @EqualsAndHashCode(callSuper = true)
    @Schema(name = "Employee.Representation")
    public static class Representation extends RepresentationModel<Representation> implements EmployeeDef.Representation {
        private UUID id;
        private String username;
        private String name;
        private String email;
        private String cpf;
        private LocalDateTime createdAt;

        public UUID getId() {
            add(linkTo(getControllerClass())
                    .slash(id)
                    .withSelfRel());
            return id;
        }

        public static EmployeeDto.Representation buildEmployeeDtoRepresentation(Employee customer) {
            return MAPPER.convertToRepresentation(customer);
        }
    }

    @Getter(onMethod_ = @Override)
    @Builder
    @NoArgsConstructor(access = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    @Schema(name = "Employee.UpdateRequest")
    public static class PatchRequest implements EmployeeDef.PatchRequest {
        private String name;
        private String email;

        public Employee buildEmployee() {
            return MAPPER.convert(this);
        }
    }
}
