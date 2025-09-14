package krashi.server.service;

import org.springframework.http.ResponseEntity;

import krashi.server.dto.EventDto;

public interface AdminService {
    ResponseEntity<?> createEvent(EventDto eventDto);
    ResponseEntity<?> updateEvent(Long eventId, EventDto eventDto);
    ResponseEntity<?> deleteEvent(Long eventId);
    ResponseEntity<?> publishEvent(Long eventId);
    ResponseEntity<?> cancelEvent(Long eventId, String reason);
    
    ResponseEntity<?> getEventStatistics(Long eventId);
    ResponseEntity<?> getEventBookings(Long eventId);
    ResponseEntity<?> getEventWaitlist(Long eventId);
    ResponseEntity<?> getEventFeedback(Long eventId);
    ResponseEntity<?> getAdminEvents();
    ResponseEntity<?> getEventDetails(Long eventId);
    
    ResponseEntity<?> notifyWaitlistUsers(Long eventId);
}
