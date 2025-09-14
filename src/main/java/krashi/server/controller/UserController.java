package krashi.server.controller;

import org.springframework.http.ResponseEntity;
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
public class UserController {
    private final UserService userService;

    @PostMapping("/book/{eventId}/{numberOfSeats}")
    public ResponseEntity<?> bookEvent(@PathVariable Long eventId, @PathVariable int numberOfSeats) {
        return userService.bookEvent(eventId, numberOfSeats);
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        return userService.cancelBooking(bookingId);
    }

    @GetMapping("/details/{bookingId}")
    public ResponseEntity<?> getBookingDetails(@PathVariable Long bookingId) {
        return userService.getBookingDetails(bookingId);
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getUserBookings() {
        return userService.getUserBookings();
    }
    
    @PostMapping("/waitlist/{eventId}/{numberOfSeats}")
    public ResponseEntity<?> joinWaitlist(@PathVariable Long eventId, @PathVariable int numberOfSeats) {
        return userService.joinWaitlist(eventId, numberOfSeats);
    }
    
    @GetMapping("/waitlist")
    public ResponseEntity<?> getUserWaitlist() {
        return userService.getUserWaitlist();
    }
    
    @DeleteMapping("/waitlist/{waitlistId}")
    public ResponseEntity<?> removeFromWaitlist(@PathVariable Long waitlistId) {
        return userService.removeFromWaitlist(waitlistId);
    }
    
    @PostMapping("/feedback")
    public ResponseEntity<?> submitEventFeedback(@RequestBody EventFeedbackDto feedbackDto) {
        return userService.submitEventFeedback(feedbackDto);
    }
    
    @GetMapping("/feedback")
    public ResponseEntity<?> getUserFeedback() {
        return userService.getUserFeedback();
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
