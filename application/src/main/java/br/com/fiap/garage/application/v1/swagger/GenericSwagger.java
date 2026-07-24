package br.com.fiap.garage.application.v1.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ProblemDetail;

@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
@ApiResponse(responseCode = "415", description = "Unsupported Media Type", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
@ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
public interface GenericSwagger {

}
