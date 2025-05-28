package krashi.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<?> createEvent(@RequestBody EventDto eventDto) {
        return adminService.createEvent(
                eventDto.getTitle(),
                eventDto.getDescription(),
                eventDto.getDate(),
                eventDto.getTime(),
                eventDto.getLocation(),
                eventDto.getTotalSeats()
        );
    }

    @PutMapping("/event/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId, @RequestBody EventDto eventDto) {
        return adminService.updateEvent(eventId, eventDto);
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        return adminService.deleteEvent(eventId);
    }
    
}
