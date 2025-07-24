package krashi.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krashi.server.dto.EventFeedbackDto;
import krashi.server.service.UserService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:4200"})
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
    
    @PostMapping("/waitlist/{eventId}/{userId}/{numberOfSeats}")
    public ResponseEntity<?> joinWaitlist(@PathVariable Long eventId, @PathVariable Long userId, @PathVariable int numberOfSeats) {
        return userService.joinWaitlist(eventId, userId, numberOfSeats);
    }
    
    @GetMapping("/waitlist/{userId}")
    public ResponseEntity<?> getUserWaitlist(@PathVariable Long userId) {
        return userService.getUserWaitlist(userId);
    }
    
    @DeleteMapping("/waitlist/{waitlistId}/{userId}")
    public ResponseEntity<?> removeFromWaitlist(@PathVariable Long waitlistId, @PathVariable Long userId) {
        return userService.removeFromWaitlist(waitlistId, userId);
    }
    
    @PostMapping("/feedback/{userId}")
    public ResponseEntity<?> submitEventFeedback(@RequestBody EventFeedbackDto feedbackDto, @PathVariable Long userId) {
        return userService.submitEventFeedback(feedbackDto, userId);
    }
    
    @GetMapping("/feedback/{userId}")
    public ResponseEntity<?> getUserFeedback(@PathVariable Long userId) {
        return userService.getUserFeedback(userId);
    }
    
    @GetMapping("/events/upcoming")
    public ResponseEntity<?> getUpcomingEvents() {
        return userService.getUpcomingEvents();
    }
    
    @GetMapping("/events/category")
    public ResponseEntity<?> getEventsByCategory(@RequestParam String category) {
        return userService.getEventsByCategory(category);
    }
    
    @GetMapping("/events/search")
    public ResponseEntity<?> searchEvents(@RequestParam String keyword) {
        return userService.searchEvents(keyword);
    }
}
