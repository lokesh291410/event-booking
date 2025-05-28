package krashi.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import krashi.server.service.UserService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/book/{eventId}/{userId}/{numberOfSeats}")
    public ResponseEntity<?> bookEvent(@PathVariable Long eventId, @PathVariable Long userId, @PathVariable int numberOfSeats) {
        return userService.bookEvent(eventId, userId, numberOfSeats);
    }

    @PostMapping("/cancel/{bookingId}/{userId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, @PathVariable Long userId) {
        return userService.cancelBooking(bookingId, userId);
    }

    @GetMapping("/details/{bookingId}/{userId}")
    public ResponseEntity<?> getBookingDetails(@PathVariable Long bookingId, @PathVariable Long userId) {
        return userService.getBookingDetails(bookingId, userId);
    }

    @GetMapping("/bookings/{userId}")
    public ResponseEntity<?> getUserBookings(@PathVariable Long userId) {
        return userService.getUserBookings(userId);
    }
}
