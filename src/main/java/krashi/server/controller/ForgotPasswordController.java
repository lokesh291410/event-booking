package krashi.server.controller;

import jakarta.validation.Valid;
import krashi.server.dto.ForgotPasswordRequestDto;
import krashi.server.dto.ForgotPasswordResponseDto;
import krashi.server.dto.VerifyOtpRequestDto;
import krashi.server.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {
    
    private final ForgotPasswordService forgotPasswordService;
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDto> sendOtp(@Valid @RequestBody ForgotPasswordRequestDto request) {
        ForgotPasswordResponseDto response = forgotPasswordService.sendOtp(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<ForgotPasswordResponseDto> verifyOtpAndResetPassword(@Valid @RequestBody VerifyOtpRequestDto request) {
        ForgotPasswordResponseDto response = forgotPasswordService.verifyOtpAndResetPassword(request);
        return ResponseEntity.ok(response);
    }
}
