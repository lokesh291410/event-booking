package krashi.server.service;

import org.springframework.http.ResponseEntity;

import krashi.server.dto.EventDto;

public interface AdminService {
    ResponseEntity<?> createEvent(EventDto eventDto, Long adminId);
    ResponseEntity<?> updateEvent(Long eventId, EventDto eventDto, Long adminId);
    ResponseEntity<?> deleteEvent(Long eventId, Long adminId);
    ResponseEntity<?> publishEvent(Long eventId, Long adminId);
    ResponseEntity<?> cancelEvent(Long eventId, String reason, Long adminId);
    
    // Analytics and management
    ResponseEntity<?> getEventStatistics(Long eventId, Long adminId);
    ResponseEntity<?> getEventBookings(Long eventId, Long adminId);
    ResponseEntity<?> getEventWaitlist(Long eventId, Long adminId);
    ResponseEntity<?> getEventFeedback(Long eventId, Long adminId);
    ResponseEntity<?> getAdminEvents(Long adminId); // Admin can see only their events
    
    // Waitlist management
    ResponseEntity<?> notifyWaitlistUsers(Long eventId, Long adminId);
}
