package krashi.server.service;

import org.springframework.http.ResponseEntity;

import krashi.server.dto.EventFeedbackDto;

public interface UserService {
    ResponseEntity<?> bookEvent(Long eventId, int numberOfSeats);
    ResponseEntity<?> cancelBooking(Long bookingId);
    ResponseEntity<?> getBookingDetails(Long bookingId);
    ResponseEntity<?> getUserBookings();
    
    ResponseEntity<?> getUserWaitlist();
    ResponseEntity<?> removeFromWaitlist(Long waitlistId);
    
    ResponseEntity<?> submitEventFeedback(EventFeedbackDto feedbackDto);
    ResponseEntity<?> getUserFeedback();
    
    ResponseEntity<?> getUpcomingEvents();
    ResponseEntity<?> getEventsByCategory(String category);
    ResponseEntity<?> searchEvents(String keyword);
}
