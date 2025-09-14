package krashi.server.service;

import krashi.server.dto.ForgotPasswordRequestDto;
import krashi.server.dto.ForgotPasswordResponseDto;
import krashi.server.dto.VerifyOtpRequestDto;

public interface ForgotPasswordService {
    ForgotPasswordResponseDto sendOtp(ForgotPasswordRequestDto request);
    ForgotPasswordResponseDto verifyOtpAndResetPassword(VerifyOtpRequestDto request);
}
