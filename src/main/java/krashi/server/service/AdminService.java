package krashi.server.service;


import org.springframework.http.ResponseEntity;

import krashi.server.dto.EventDto;

public interface AdminService {
    ResponseEntity<?> createEvent(String title, String description,String date, String time, String location, int totalSeats);
    ResponseEntity<?> updateEvent(Long eventId, EventDto eventDto);
    ResponseEntity<?> deleteEvent(Long eventId);
}
