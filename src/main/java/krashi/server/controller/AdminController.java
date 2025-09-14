package krashi.server.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import krashi.server.dto.EventDto;
import krashi.server.service.AdminService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/event")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventDto eventDto) {
        return adminService.createEvent(eventDto);
    }

    @PutMapping("/event/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId, @Valid @RequestBody EventDto eventDto) {
        return adminService.updateEvent(eventId, eventDto);
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        return adminService.deleteEvent(eventId);
    }
    
    @PostMapping("/event/{eventId}/publish")
    public ResponseEntity<?> publishEvent(@PathVariable Long eventId) {
        return adminService.publishEvent(eventId);
    }
    
    @PostMapping("/event/{eventId}/cancel")
    public ResponseEntity<?> cancelEvent(@PathVariable Long eventId, @RequestParam String reason) {
        return adminService.cancelEvent(eventId, reason);
    }
    
    @GetMapping("/event/{eventId}/statistics")
    public ResponseEntity<?> getEventStatistics(@PathVariable Long eventId) {
        return adminService.getEventStatistics(eventId);
    }
    
    @GetMapping("/event/{eventId}/bookings")
    public ResponseEntity<?> getEventBookings(@PathVariable Long eventId) {
        return adminService.getEventBookings(eventId);
    }
    
    @GetMapping("/event/{eventId}/waitlist")
    public ResponseEntity<?> getEventWaitlist(@PathVariable Long eventId) {
        return adminService.getEventWaitlist(eventId);
    }
    
    @GetMapping("/event/{eventId}/feedback")
    public ResponseEntity<?> getEventFeedback(@PathVariable Long eventId) {
        return adminService.getEventFeedback(eventId);
    }
    
    @GetMapping("/events")
    public ResponseEntity<?> getAdminEvents() {
        return adminService.getAdminEvents();
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getEventDetails(@PathVariable Long eventId) {
        return adminService.getEventDetails(eventId);
    }
    
    @PostMapping("/event/{eventId}/notify-waitlist")
    public ResponseEntity<?> notifyWaitlistUsers(@PathVariable Long eventId) {
        return adminService.notifyWaitlistUsers(eventId);
    }
    
}
