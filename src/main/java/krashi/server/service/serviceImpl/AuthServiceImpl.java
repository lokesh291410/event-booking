package krashi.server.service.serviceImpl;

import krashi.server.dto.LoginRequest;
import krashi.server.dto.LoginResponse;
import krashi.server.entity.UserInfo;
import krashi.server.exception.BadRequestException;
import krashi.server.repository.UserInfoRepository;
import krashi.server.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        // Validate input
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        // Find user by username
        UserInfo user = userInfoRepository.findByUserName(loginRequest.getUsername())
                .orElse(null);

        // Check if user exists and password matches
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.ok(new LoginResponse(false, "Invalid username or password"));
        }

        // Successful login
        LoginResponse response = new LoginResponse(
            true,
            "Login successful",
            user.getId(),
            user.getUserName(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}
