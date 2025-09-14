package krashi.server.service;

public interface OtpService {
    String generateOtp();
    void storeOtp(String email, String otp);
    boolean validateOtp(String email, String otp);
    void deleteOtp(String email);
}
