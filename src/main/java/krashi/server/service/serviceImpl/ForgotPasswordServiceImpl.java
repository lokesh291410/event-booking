package krashi.server.service.serviceImpl;

import krashi.server.dto.ForgotPasswordRequestDto;
import krashi.server.dto.ForgotPasswordResponseDto;
import krashi.server.dto.VerifyOtpRequestDto;
import krashi.server.entity.UserInfo;
import krashi.server.exception.InvalidOtpException;
import krashi.server.exception.ResourceNotFoundException;
import krashi.server.repository.UserInfoRepository;
import krashi.server.service.EmailService;
import krashi.server.service.ForgotPasswordService;
import krashi.server.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    
    private final UserInfoRepository userInfoRepository;
    private final EmailService emailService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public ForgotPasswordResponseDto sendOtp(ForgotPasswordRequestDto request) {
        String email = request.getEmail().toLowerCase().trim();
        
        userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));
        
        String otp = otpService.generateOtp();
        
        otpService.storeOtp(email, otp);
        
        emailService.sendOtpEmail(email, otp);
        
        log.info("Password reset OTP sent successfully to email: {}", email);
        return new ForgotPasswordResponseDto("OTP sent successfully to your email address", true);
    }
    
    @Override
    @Transactional
    public ForgotPasswordResponseDto verifyOtpAndResetPassword(VerifyOtpRequestDto request) {
        String email = request.getEmail().toLowerCase().trim();
        String otp = request.getOtp();
        String newPassword = request.getNewPassword();
        
        UserInfo user = userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));
        
        boolean isOtpValid = otpService.validateOtp(email, otp);
        
        if (!isOtpValid) {
            throw new InvalidOtpException("Invalid or expired OTP. Please request a new OTP.");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userInfoRepository.save(user);
        
        log.info("Password reset successfully for email: {}", email);
        return new ForgotPasswordResponseDto("Password reset successfully", true);
    }
}
