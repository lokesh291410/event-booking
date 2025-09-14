package krashi.server.service.serviceImpl;

import krashi.server.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String OTP_PREFIX = "otp:";
    private static final Duration OTP_EXPIRY = Duration.ofMinutes(5);
    private static final SecureRandom secureRandom = new SecureRandom();
    
    @Override
    public String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }
    
    @Override
    public void storeOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRY);
        log.info("OTP stored for email: {} with expiry: {} minutes", email, OTP_EXPIRY.toMinutes());
    }
    
    @Override
    public boolean validateOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        String storedOtp = (String) redisTemplate.opsForValue().get(key);
        
        if (storedOtp == null) {
            log.warn("OTP not found or expired for email: {}", email);
            return false;
        }
        
        boolean isValid = storedOtp.equals(otp);
        if (isValid) {
            redisTemplate.delete(key);
            log.info("OTP validated and deleted for email: {}", email);
        } else {
            log.warn("Invalid OTP provided for email: {}", email);
        }
        
        return isValid;
    }
    
    @Override
    public void deleteOtp(String email) {
        String key = OTP_PREFIX + email;
        redisTemplate.delete(key);
        log.info("OTP deleted for email: {}", email);
    }
}
