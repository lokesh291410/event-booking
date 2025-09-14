package krashi.server.controller;

import jakarta.validation.Valid;
import krashi.server.dto.LoginRequest;
import krashi.server.dto.LoginResponse;
import krashi.server.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return jwtService.login(loginRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return jwtService.logout();
    }
}
