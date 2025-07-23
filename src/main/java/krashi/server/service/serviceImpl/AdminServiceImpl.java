package krashi.server.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import krashi.server.dto.EventDto;
import krashi.server.entity.Booking;
import krashi.server.entity.Event;
import krashi.server.entity.EventFeedback;
import krashi.server.entity.UserInfo;
import krashi.server.entity.Waitlist;
import krashi.server.exception.AccessDeniedException;
import krashi.server.exception.BadRequestException;
import krashi.server.exception.ResourceNotFoundException;
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
        UserInfo admin = userInfoRepository.findById(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin with ID " + adminId + " not found"));
        
        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        
        LocalDate eventDate = LocalDate.parse(eventDto.getDate());
        LocalTime eventTime = LocalTime.parse(eventDto.getTime());
        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);
        
        // Validate event date is in the future
        if (eventDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event date and time must be in the future");
        }
        
        event.setDateTime(eventDateTime);
        
        if (eventDto.getEndDate() != null && eventDto.getEndTime() != null) {
            LocalDate endDate = LocalDate.parse(eventDto.getEndDate());
            LocalTime endTime = LocalTime.parse(eventDto.getEndTime());
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            
            if (endDateTime.isBefore(eventDateTime)) {
                throw new BadRequestException("End date and time must be after start date and time");
            }
            
            event.setEndDateTime(endDateTime);
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
        event.setCreatedBy(admin);

        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.ok("Event created successfully with ID: " + savedEvent.getId());
    }

    @Override
    public ResponseEntity<?> updateEvent(Long eventId, EventDto eventDto, Long adminId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found"));
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            throw new AccessDeniedException("You can only update events you created");
        }
        
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        
        LocalDate eventDate = LocalDate.parse(eventDto.getDate());
        LocalTime eventTime = LocalTime.parse(eventDto.getTime());
        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);
        
        // Validate event date is in the future (unless already published)
        if (!event.getStatus().equals("PUBLISHED") && eventDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event date and time must be in the future");
        }
        
        event.setDateTime(eventDateTime);
        
        if (eventDto.getEndDate() != null && eventDto.getEndTime() != null) {
            LocalDate endDate = LocalDate.parse(eventDto.getEndDate());
            LocalTime endTime = LocalTime.parse(eventDto.getEndTime());
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            
            if (endDateTime.isBefore(eventDateTime)) {
                throw new BadRequestException("End date and time must be after start date and time");
            }
            
            event.setEndDateTime(endDateTime);
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
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            throw new AccessDeniedException("You can only delete events you created");
        }
        
        // Check if event has bookings
        List<Booking> bookings = bookingRepository.findByEvent_Id(eventId);
        if (!bookings.isEmpty()) {
            throw new BadRequestException("Cannot delete event with existing bookings. Cancel the event instead.");
        }
        
        eventRepository.deleteById(eventId);
        return ResponseEntity.ok("Event deleted successfully");
    }

    @Override
    public ResponseEntity<?> publishEvent(Long eventId, Long adminId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            throw new AccessDeniedException("You can only publish events you created");
        }
        
        // Validate event can be published
        if (!event.getStatus().equals("DRAFT")) {
            throw new BadRequestException("Only draft events can be published");
        }
        
        if (event.getDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot publish event with past date");
        }
        
        event.setStatus("PUBLISHED");
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
        
        return ResponseEntity.ok("Event published successfully");
    }

    @Override
    public ResponseEntity<?> cancelEvent(Long eventId, String reason, Long adminId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found"));
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            throw new AccessDeniedException("You can only cancel events you created");
        }
        
        event.setStatus("CANCELLED");
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
        
        return ResponseEntity.ok("Event cancelled successfully. Reason: " + reason);
    }

    @Override
    public ResponseEntity<?> getEventStatistics(Long eventId, Long adminId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found"));
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            throw new AccessDeniedException("You can only view statistics for events you created");
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
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found"));
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            throw new AccessDeniedException("You can only view bookings for events you created");
        }
        
        List<Booking> bookings = bookingRepository.findByEvent_Id(eventId);
        return ResponseEntity.ok(bookings);
    }

    @Override
    public ResponseEntity<?> getEventWaitlist(Long eventId, Long adminId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found"));
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            throw new AccessDeniedException("You can only view waitlist for events you created");
        }
        
        List<Waitlist> waitlist = waitlistRepository.findByEventIdAndStatus(eventId, "WAITING");
        return ResponseEntity.ok(waitlist);
    }

    @Override
    public ResponseEntity<?> getEventFeedback(Long eventId, Long adminId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found"));
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            throw new AccessDeniedException("You can only view feedback for events you created");
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
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found"));
        
        // Check if the admin is the creator of the event
        if (event.getCreatedBy() == null || !event.getCreatedBy().getId().equals(adminId)) {
            throw new AccessDeniedException("You can only notify waitlist users for events you created");
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
