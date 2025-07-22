package krashi.server.service;

import org.springframework.http.ResponseEntity;

import krashi.server.dto.EventFeedbackDto;

public interface UserService {
    ResponseEntity<?> bookEvent(Long eventId, Long userId, int numberOfSeats);
    ResponseEntity<?> cancelBooking(Long bookingId, Long userId);
    ResponseEntity<?> getBookingDetails(Long bookingId, Long userId);
    ResponseEntity<?> getUserBookings(Long userId);
    
    // Waitlist functionality
    ResponseEntity<?> joinWaitlist(Long eventId, Long userId, int numberOfSeats);
    ResponseEntity<?> getUserWaitlist(Long userId);
    ResponseEntity<?> removeFromWaitlist(Long waitlistId, Long userId);
    
    // Feedback functionality
    ResponseEntity<?> submitEventFeedback(EventFeedbackDto feedbackDto, Long userId);
    ResponseEntity<?> getUserFeedback(Long userId);
    
    // Event browsing
    ResponseEntity<?> getUpcomingEvents();
    ResponseEntity<?> getEventsByCategory(String category);
    ResponseEntity<?> searchEvents(String keyword);
}
