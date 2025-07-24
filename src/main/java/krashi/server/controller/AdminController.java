package krashi.server.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:4200"})
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/event")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventDto eventDto, @RequestParam Long adminId) {
        return adminService.createEvent(eventDto, adminId);
    }

    @PutMapping("/event/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId, @Valid @RequestBody EventDto eventDto, @RequestParam Long adminId) {
        return adminService.updateEvent(eventId, eventDto, adminId);
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId, @RequestParam Long adminId) {
        return adminService.deleteEvent(eventId, adminId);
    }
    
    @PostMapping("/event/{eventId}/publish")
    public ResponseEntity<?> publishEvent(@PathVariable Long eventId, @RequestParam Long adminId) {
        return adminService.publishEvent(eventId, adminId);
    }
    
    @PostMapping("/event/{eventId}/cancel")
    public ResponseEntity<?> cancelEvent(@PathVariable Long eventId, @RequestParam String reason, @RequestParam Long adminId) {
        return adminService.cancelEvent(eventId, reason, adminId);
    }
    
    @GetMapping("/event/{eventId}/statistics")
    public ResponseEntity<?> getEventStatistics(@PathVariable Long eventId, @RequestParam Long adminId) {
        return adminService.getEventStatistics(eventId, adminId);
    }
    
    @GetMapping("/event/{eventId}/bookings")
    public ResponseEntity<?> getEventBookings(@PathVariable Long eventId, @RequestParam Long adminId) {
        return adminService.getEventBookings(eventId, adminId);
    }
    
    @GetMapping("/event/{eventId}/waitlist")
    public ResponseEntity<?> getEventWaitlist(@PathVariable Long eventId, @RequestParam Long adminId) {
        return adminService.getEventWaitlist(eventId, adminId);
    }
    
    @GetMapping("/event/{eventId}/feedback")
    public ResponseEntity<?> getEventFeedback(@PathVariable Long eventId, @RequestParam Long adminId) {
        return adminService.getEventFeedback(eventId, adminId);
    }
    
    @GetMapping("/events")
    public ResponseEntity<?> getAdminEvents(@RequestParam Long adminId) {
        return adminService.getAdminEvents(adminId);
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getEventDetails(@PathVariable Long eventId, @RequestParam Long adminId) {
        return adminService.getEventDetails(eventId, adminId);
    }
    
    @PostMapping("/event/{eventId}/notify-waitlist")
    public ResponseEntity<?> notifyWaitlistUsers(@PathVariable Long eventId, @RequestParam Long adminId) {
        return adminService.notifyWaitlistUsers(eventId, adminId);
    }
    
}
