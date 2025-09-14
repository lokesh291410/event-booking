package krashi.server.service.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import krashi.server.dto.LoginRequest;
import krashi.server.dto.LoginResponse;
import krashi.server.exception.InvalidCredentialsException;
import krashi.server.exception.JwtTokenExpiredException;
import krashi.server.service.JwtService;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final AuthenticationManager authenticationManager;
    
    public JwtServiceImpl(@Lazy AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new InvalidCredentialsException("Username is required");
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new InvalidCredentialsException("Password is required");
        }
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        if(authentication.isAuthenticated()) {
            String token = generateToken(loginRequest.getUsername());

            ResponseCookie tokenCookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(300)
                .sameSite("Strict")
                .build();

            LoginResponse response = new LoginResponse(
                true,
                "Login successful",
                loginRequest.getUsername()
            );

            return ResponseEntity.ok()
                .header("jwt-cookie", tokenCookie.toString())
                .body(response);
        } else {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @Override
    public String generateToken(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidCredentialsException("Username is required for token generation");
        }
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignedKey()).compact();
    }

    private SecretKey getSignedKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUsername(String jwtToken) {
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new InvalidCredentialsException("JWT token is required");
        }
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    public boolean isTokenExpired(String jwtToken) {
        Date expiration = extractExpiration(jwtToken);
        if (expiration.before(new Date())) {
            throw new JwtTokenExpiredException("JWT token has expired");
        }
        return false;
    }

    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getSignedKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    @Override
    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        if (jwtToken == null || userDetails == null) {
            throw new InvalidCredentialsException("Invalid token or user details");
        }
        
        String username = extractUsername(jwtToken);
        if (!username.equals(userDetails.getUsername())) {
            throw new InvalidCredentialsException("Token username does not match user");
        }
        
        isTokenExpired(jwtToken);
        
        return true;
    }

    public ResponseEntity<?> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();

        return ResponseEntity.ok()
            .header("jwt-cookie", deleteCookie.toString())
            .body("Logged out successfully");
    }
    
}
