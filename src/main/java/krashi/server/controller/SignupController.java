package krashi.server.controller;

import krashi.server.dto.UserInfoDto;
import krashi.server.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:4200"})
public class SignupController {

    @Autowired
    SignUpService signUpService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserInfoDto userInfoDto) {
        return signUpService.signUp(
                userInfoDto.getUsername(),
                userInfoDto.getName(),
                userInfoDto.getEmail(),
                userInfoDto.getPassword(),
                userInfoDto.getRole()
        );
    }

    @GetMapping("/events")
    public ResponseEntity<?> getAllEvents() {
        return signUpService.getAllEvents();
    }
    
    @GetMapping("/events/published")
    public ResponseEntity<?> getPublishedEvents() {
        return signUpService.getPublishedEvents();
    }
    
    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> getEventDetails(@PathVariable Long eventId) {
        return signUpService.getEventDetails(eventId);
    }
    
    @GetMapping("/events/categories")
    public ResponseEntity<?> getEventCategories() {
        return signUpService.getEventCategories();
    }
}
