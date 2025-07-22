package krashi.server.service;

import org.springframework.http.ResponseEntity;

public interface SignUpService {
    ResponseEntity<?> signUp(String username, String name, String email, String password, String role);
    ResponseEntity<?> getAllEvents();
    ResponseEntity<?> getPublishedEvents();
    ResponseEntity<?> getEventDetails(Long eventId);
    ResponseEntity<?> getEventCategories();
}
