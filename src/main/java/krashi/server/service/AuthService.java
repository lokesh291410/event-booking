package krashi.server.service;

import krashi.server.dto.LoginRequest;
import krashi.server.dto.LoginResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);
}
