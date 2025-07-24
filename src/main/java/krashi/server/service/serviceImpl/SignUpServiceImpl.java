package krashi.server.service.serviceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

import krashi.server.dto.UserEventDetailDto;
import krashi.server.entity.Event;
import krashi.server.entity.EventFeedback;
import krashi.server.entity.UserInfo;
import krashi.server.entity.Waitlist;
import krashi.server.repository.EventFeedbackRepository;
import krashi.server.repository.EventRepository;
import krashi.server.repository.UserInfoRepository;
import krashi.server.repository.WaitlistRepository;
import krashi.server.service.SignUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpServiceImpl implements SignUpService {

    UserInfoRepository userInfoRepository;
    EventRepository eventRepository;
    EventFeedbackRepository eventFeedbackRepository;
    WaitlistRepository waitlistRepository;
    PasswordEncoder passwordEncoder;

    public SignUpServiceImpl(UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder, 
                           EventRepository eventRepository, EventFeedbackRepository eventFeedbackRepository,
                           WaitlistRepository waitlistRepository) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventRepository = eventRepository;
        this.eventFeedbackRepository = eventFeedbackRepository;
        this.waitlistRepository = waitlistRepository;
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
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = eventOpt.get();
        
        // Only show published events to users
        if (!"PUBLISHED".equals(event.getStatus())) {
            return ResponseEntity.notFound().build();
        }
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        UserEventDetailDto details = new UserEventDetailDto();
        
        // Basic event information
        details.setId(event.getId());
        details.setTitle(event.getTitle());
        details.setDescription(event.getDescription());
        
        if (event.getDateTime() != null) {
            details.setDate(event.getDateTime().toLocalDate().format(dateFormatter));
            details.setTime(event.getDateTime().toLocalTime().format(timeFormatter));
        }
        
        if (event.getEndDateTime() != null) {
            details.setEndDate(event.getEndDateTime().toLocalDate().format(dateFormatter));
            details.setEndTime(event.getEndDateTime().toLocalTime().format(timeFormatter));
        }
        
        details.setLocation(event.getLocation());
        details.setTotalSeats(event.getTotalSeats());
        details.setAvailableSeats(event.getAvailableSeats());
        details.setCategory(event.getCategory());
        details.setImageUrl(event.getImageUrl());
        details.setPrice(event.getPrice());
        details.setOrganizerName(event.getOrganizerName());
        details.setOrganizerEmail(event.getOrganizerEmail());
        details.setStatus(event.getStatus());
        
        // User-relevant statistics
        details.setBookedSeats(event.getTotalSeats() - event.getAvailableSeats());
        details.setAvailable(event.getAvailableSeats() > 0);
        
        // Get waitlist information
        List<Waitlist> waitlist = waitlistRepository.findByEventIdAndStatus(eventId, "WAITING");
        details.setWaitlistCount(waitlist.size());
        details.setHasWaitlist(waitlist.size() > 0);
        
        // Get feedback information
        List<EventFeedback> feedbacks = eventFeedbackRepository.findByEventId(eventId);
        Double averageRating = eventFeedbackRepository.getAverageRatingByEventId(eventId);
        details.setAverageRating(averageRating != null ? averageRating : 0.0);
        details.setTotalFeedbacks(feedbacks.size());
        
        // Recent public feedbacks (last 5, for user reference)
        List<UserEventDetailDto.PublicFeedbackDto> recentFeedbacks = feedbacks.stream()
                .sorted((f1, f2) -> f2.getSubmittedAt().compareTo(f1.getSubmittedAt()))
                .limit(5)
                .map(f -> {
                    UserEventDetailDto.PublicFeedbackDto dto = new UserEventDetailDto.PublicFeedbackDto();
                    dto.setUserName(f.getUser().getUserName());
                    dto.setRating(f.getRating());
                    dto.setComment(f.getComment());
                    dto.setWouldRecommend(f.isWouldRecommend());
                    dto.setSubmittedAt(f.getSubmittedAt());
                    return dto;
                })
                .collect(Collectors.toList());
        details.setRecentFeedbacks(recentFeedbacks);
        
        return ResponseEntity.ok(details);
    }

    @Override
    public ResponseEntity<?> getEventCategories() {
        List<String> categories = Arrays.asList(
            "WORKSHOP", "CONFERENCE", "HACKATHON", "MEETUP", "WEBINAR", "SEMINAR"
        );
        return ResponseEntity.ok(categories);
    }
}
