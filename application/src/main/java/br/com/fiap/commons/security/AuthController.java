package br.com.fiap.commons.security;

import br.com.fiap.garage.application.v1.swagger.AuthSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/auth")
public class AuthController implements AuthSwagger {

    private final LoginService loginService;

    private final TokenService tokenService;

    @PostMapping(path = "/login",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody
            LoginRequestDto data) {
        var userDetails = loginService.loadUserByUsername(data.username);
        var token = tokenService.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    public record LoginRequestDto(String username, String password) {}

    public record LoginResponseDto(String token) {}
}
