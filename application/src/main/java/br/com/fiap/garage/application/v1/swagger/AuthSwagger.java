package br.com.fiap.garage.application.v1.swagger;

import br.com.fiap.commons.security.AuthController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "WorkOrder (v1)", description = "WorkOrder resource.")
public interface AuthSwagger extends GenericSwagger {

    @Operation(summary = "User authentication.")
    @ApiResponse(responseCode = "200", description = "OK")
    ResponseEntity<AuthController.LoginResponseDto> login(
            AuthController.LoginRequestDto data);
}
