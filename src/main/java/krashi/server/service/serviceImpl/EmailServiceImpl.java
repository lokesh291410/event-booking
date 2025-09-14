package krashi.server.service.serviceImpl;

import krashi.server.exception.EmailSendingException;
import krashi.server.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP - Krashi Event Booking");
            message.setText(buildOtpEmailContent(otp));
            
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            throw new EmailSendingException("Failed to send OTP email. Please try again later.");
        }
    }
    
    private String buildOtpEmailContent(String otp) {
        return String.format(
            "Dear User,\n\n" +
            "You have requested to reset your password for Krashi Event Booking.\n\n" +
            "Your OTP is: %s\n\n" +
            "This OTP is valid for 5 minutes only. Please do not share this OTP with anyone.\n\n" +
            "If you did not request this password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Krashi Event Booking Team",
            otp
        );
    }
}
