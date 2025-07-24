package krashi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String name;
    private String email;
    private String role;
    
    // Constructor for failed login
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
