package krashi.server.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import krashi.server.dto.LoginRequest;
import krashi.server.dto.LoginResponse;

public interface JwtService {
    String generateToken(String username);
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);
    String extractUsername(String jwtToken);
    boolean isTokenValid(String jwtToken, UserDetails userDetails);
    ResponseEntity<?> logout();
}
