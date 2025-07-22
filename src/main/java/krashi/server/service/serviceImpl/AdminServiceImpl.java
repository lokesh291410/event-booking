package krashi.server.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import krashi.server.dto.EventDto;
import krashi.server.entity.Booking;
import krashi.server.entity.Event;
import krashi.server.entity.EventFeedback;
import krashi.server.entity.UserInfo;
import krashi.server.entity.Waitlist;
import krashi.server.repository.BookingRepository;
import krashi.server.repository.EventFeedbackRepository;
import krashi.server.repository.EventRepository;
import krashi.server.repository.UserInfoRepository;
import krashi.server.repository.WaitlistRepository;
import krashi.server.service.AdminService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final WaitlistRepository waitlistRepository;
    private final EventFeedbackRepository eventFeedbackRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public ResponseEntity<?> createEvent(EventDto eventDto, Long adminId) {
        // Verify admin exists
        Optional<UserInfo> adminOpt = userInfoRepository.findById(adminId);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Admin not found");
        }
        
        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        
        LocalDate eventDate = LocalDate.parse(eventDto.getDate());
        LocalTime eventTime = LocalTime.parse(eventDto.getTime());
        event.setDateTime(LocalDateTime.of(eventDate, eventTime));
        
        if (eventDto.getEndDate() != null && eventDto.getEndTime() != null) {
            LocalDate endDate = LocalDate.parse(eventDto.getEndDate());
            LocalTime endTime = LocalTime.parse(eventDto.getEndTime());
            event.setEndDateTime(LocalDateTime.of(endDate, endTime));
        }
        
        event.setLocation(eventDto.getLocation());
        event.setTotalSeats(eventDto.getTotalSeats());
        event.setAvailableSeats(eventDto.getTotalSeats());
        event.setCategory(eventDto.getCategory());
        event.setImageUrl(eventDto.getImageUrl());
        event.setPrice(eventDto.getPrice());
        event.setOrganizerName(eventDto.getOrganizerName());
        event.setOrganizerEmail(eventDto.getOrganizerEmail());
        event.setStatus("DRAFT");
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setCreatedBy(adminOpt.get()); // Set the creator

        eventRepository.save(event);
        return ResponseEntity.ok("Event created successfully with ID: " + event.getId());
    }

    @Override
    public ResponseEntity<?> updateEvent(Long eventId, EventDto eventDto, Long adminId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = optionalEvent.get();
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            return ResponseEntity.status(403).body("Access denied: You can only update events you created");
        }
        
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        
        LocalDate eventDate = LocalDate.parse(eventDto.getDate());
        LocalTime eventTime = LocalTime.parse(eventDto.getTime());
        event.setDateTime(LocalDateTime.of(eventDate, eventTime));
        
        if (eventDto.getEndDate() != null && eventDto.getEndTime() != null) {
            LocalDate endDate = LocalDate.parse(eventDto.getEndDate());
            LocalTime endTime = LocalTime.parse(eventDto.getEndTime());
            event.setEndDateTime(LocalDateTime.of(endDate, endTime));
        }
        
        event.setLocation(eventDto.getLocation());
        event.setCategory(eventDto.getCategory());
        event.setImageUrl(eventDto.getImageUrl());
        event.setPrice(eventDto.getPrice());
        event.setOrganizerName(eventDto.getOrganizerName());
        event.setOrganizerEmail(eventDto.getOrganizerEmail());
        event.setUpdatedAt(LocalDateTime.now());

        // Only update total seats if not published yet
        if (event.getStatus().equals("DRAFT")) {
            event.setTotalSeats(eventDto.getTotalSeats());
            event.setAvailableSeats(eventDto.getTotalSeats());
        }

        eventRepository.save(event);
        return ResponseEntity.ok("Event updated successfully");
    }

    @Override
    public ResponseEntity<?> deleteEvent(Long eventId, Long adminId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = optionalEvent.get();
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            return ResponseEntity.status(403).body("Access denied: You can only delete events you created");
        }
        
        eventRepository.deleteById(eventId);
        return ResponseEntity.ok("Event deleted successfully");
    }

    @Override
    public ResponseEntity<?> publishEvent(Long eventId, Long adminId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = optionalEvent.get();
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            return ResponseEntity.status(403).body("Access denied: You can only publish events you created");
        }
        
        event.setStatus("PUBLISHED");
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
        
        return ResponseEntity.ok("Event published successfully");
    }

    @Override
    public ResponseEntity<?> cancelEvent(Long eventId, String reason, Long adminId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = optionalEvent.get();
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            return ResponseEntity.status(403).body("Access denied: You can only cancel events you created");
        }
        
        event.setStatus("CANCELLED");
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
        
        return ResponseEntity.ok("Event cancelled successfully. Reason: " + reason);
    }

    @Override
    public ResponseEntity<?> getEventStatistics(Long eventId, Long adminId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = optionalEvent.get();
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            return ResponseEntity.status(403).body("Access denied: You can only view statistics for events you created");
        }
        
        List<Booking> bookings = bookingRepository.findByEvent_Id(eventId);
        List<Waitlist> waitlist = waitlistRepository.findByEventIdAndStatus(eventId, "WAITING");
        Double averageRating = eventFeedbackRepository.getAverageRatingByEventId(eventId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("event", event);
        stats.put("totalBookings", bookings.size());
        stats.put("bookedSeats", event.getTotalSeats() - event.getAvailableSeats());
        stats.put("availableSeats", event.getAvailableSeats());
        stats.put("waitlistCount", waitlist.size());
        stats.put("averageRating", averageRating != null ? averageRating : 0.0);
        
        return ResponseEntity.ok(stats);
    }

    @Override
    public ResponseEntity<?> getEventBookings(Long eventId, Long adminId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = optionalEvent.get();
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            return ResponseEntity.status(403).body("Access denied: You can only view bookings for events you created");
        }
        
        List<Booking> bookings = bookingRepository.findByEvent_Id(eventId);
        return ResponseEntity.ok(bookings);
    }

    @Override
    public ResponseEntity<?> getEventWaitlist(Long eventId, Long adminId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = optionalEvent.get();
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            return ResponseEntity.status(403).body("Access denied: You can only view waitlist for events you created");
        }
        
        List<Waitlist> waitlist = waitlistRepository.findByEventIdAndStatus(eventId, "WAITING");
        return ResponseEntity.ok(waitlist);
    }

    @Override
    public ResponseEntity<?> getEventFeedback(Long eventId, Long adminId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = optionalEvent.get();
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            return ResponseEntity.status(403).body("Access denied: You can only view feedback for events you created");
        }
        
        List<EventFeedback> feedback = eventFeedbackRepository.findByEventId(eventId);
        return ResponseEntity.ok(feedback);
    }

    @Override
    public ResponseEntity<?> getAdminEvents(Long adminId) {
        List<Event> events = eventRepository.findByCreatedBy_Id(adminId);
        return ResponseEntity.ok(events);
    }

    @Override
    public ResponseEntity<?> notifyWaitlistUsers(Long eventId, Long adminId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Event event = optionalEvent.get();
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            return ResponseEntity.status(403).body("Access denied: You can only notify waitlist users for events you created");
        }
        
        List<Waitlist> waitlist = waitlistRepository.findByEventIdAndStatus(eventId, "WAITING");
        
        for (Waitlist wait : waitlist) {
            wait.setStatus("NOTIFIED");
            wait.setNotifiedAt(LocalDateTime.now());
            waitlistRepository.save(wait);
        }
        
        return ResponseEntity.ok("Notified " + waitlist.size() + " users on waitlist");
    }

}
