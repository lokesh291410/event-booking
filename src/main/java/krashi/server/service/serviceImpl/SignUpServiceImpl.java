package krashi.server.service.serviceImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import krashi.server.entity.Event;
import krashi.server.entity.UserInfo;
import krashi.server.repository.EventRepository;
import krashi.server.repository.UserInfoRepository;
import krashi.server.service.SignUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpServiceImpl implements SignUpService {

    UserInfoRepository userInfoRepository;
    EventRepository eventRepository;
    PasswordEncoder passwordEncoder;

    public SignUpServiceImpl(UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder, EventRepository eventRepository) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventRepository = eventRepository;
    }

    @Override
    public ResponseEntity<?> signUp(String username, String name, String email, String password, String role) {
        UserInfo user = new UserInfo();
        user.setUserName(username);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_" + role);

        userInfoRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @Override
    public ResponseEntity<?> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getPublishedEvents() {
        List<Event> events = eventRepository.findByStatus("PUBLISHED");
        return ResponseEntity.ok(events);
    }

    @Override
    public ResponseEntity<?> getEventDetails(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("event", event.get());
        eventDetails.put("availableSeats", event.get().getAvailableSeats());
        eventDetails.put("bookedSeats", event.get().getTotalSeats() - event.get().getAvailableSeats());
        
        return ResponseEntity.ok(eventDetails);
    }

    @Override
    public ResponseEntity<?> getEventCategories() {
        List<String> categories = Arrays.asList(
            "WORKSHOP", "CONFERENCE", "HACKATHON", "MEETUP", "WEBINAR", "SEMINAR"
        );
        return ResponseEntity.ok(categories);
    }
}
