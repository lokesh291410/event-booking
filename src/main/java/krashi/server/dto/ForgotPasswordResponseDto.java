package krashi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForgotPasswordResponseDto {
    private String message;
    private boolean success;
}
