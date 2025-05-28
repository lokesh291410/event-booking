package krashi.server.service;

import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> bookEvent(Long eventId, Long userId, int numberOfSeats);
    ResponseEntity<?> cancelBooking(Long bookingId, Long userId);
    ResponseEntity<?> getBookingDetails(Long bookingId, Long userId);
    ResponseEntity<?> getUserBookings(Long userId);
}
