package krashi.server.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
}
