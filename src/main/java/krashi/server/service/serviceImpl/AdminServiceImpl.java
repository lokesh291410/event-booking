package krashi.server.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import krashi.server.dto.EventDto;
import krashi.server.entity.Event;
import krashi.server.repository.EventRepository;
import krashi.server.service.AdminService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final EventRepository eventRepository;

    @Override
    public ResponseEntity<?> createEvent(String title, String description, String date, String time, String location, int totalSeats) {

        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        LocalDate eventDate = LocalDate.parse(date);
        LocalTime eventTime = LocalTime.parse(time);
        event.setDateTime(LocalDateTime.of(eventDate, eventTime));
        event.setLocation(location);
        event.setTotalSeats(totalSeats);
        event.setAvailableSeats(totalSeats);

        eventRepository.save(event);

        return ResponseEntity.ok("Event created successfully");
    }

    @Override
    public ResponseEntity<?> updateEvent(Long eventId, EventDto eventDto) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        LocalDate eventDate = LocalDate.parse(eventDto.getDate());
        LocalTime eventTime = LocalTime.parse(eventDto.getTime());
        event.setDateTime(LocalDateTime.of(eventDate, eventTime));
        event.setLocation(eventDto.getLocation());
        event.setTotalSeats(eventDto.getTotalSeats());

        eventRepository.save(event);
        return ResponseEntity.ok("Event updated successfully");
    }

    @Override
    public ResponseEntity<?> deleteEvent(Long eventId) {
        if(!eventRepository.existsById(eventId)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.deleteById(eventId);
        return ResponseEntity.ok("Event deleted successfully");
    }

}
