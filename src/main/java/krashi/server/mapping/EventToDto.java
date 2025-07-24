package krashi.server.mapping;

import krashi.server.dto.EventDto;
import krashi.server.dto.EventResponseDto;
import krashi.server.entity.Event;

import java.time.format.DateTimeFormatter;

public class EventToDto {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public static EventDto mapToDto(Event event) {
        if (event == null) {
            return null;
        }
        
        EventDto dto = new EventDto();
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        
        if (event.getDateTime() != null) {
            dto.setDate(event.getDateTime().toLocalDate().format(DATE_FORMATTER));
            dto.setTime(event.getDateTime().toLocalTime().format(TIME_FORMATTER));
        }
        
        if (event.getEndDateTime() != null) {
            dto.setEndDate(event.getEndDateTime().toLocalDate().format(DATE_FORMATTER));
            dto.setEndTime(event.getEndDateTime().toLocalTime().format(TIME_FORMATTER));
        }
        
        dto.setLocation(event.getLocation());
        dto.setTotalSeats(event.getTotalSeats());
        dto.setCategory(event.getCategory());
        dto.setImageUrl(event.getImageUrl());
        dto.setPrice(event.getPrice());
        dto.setOrganizerName(event.getOrganizerName());
        dto.setOrganizerEmail(event.getOrganizerEmail());
        
        return dto;
    }
    
    public static EventResponseDto mapToResponseDto(Event event) {
        if (event == null) {
            return null;
        }
        
        EventResponseDto dto = new EventResponseDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        
        if (event.getDateTime() != null) {
            dto.setDate(event.getDateTime().toLocalDate().format(DATE_FORMATTER));
            dto.setTime(event.getDateTime().toLocalTime().format(TIME_FORMATTER));
        }
        
        if (event.getEndDateTime() != null) {
            dto.setEndDate(event.getEndDateTime().toLocalDate().format(DATE_FORMATTER));
            dto.setEndTime(event.getEndDateTime().toLocalTime().format(TIME_FORMATTER));
        }
        
        dto.setLocation(event.getLocation());
        dto.setTotalSeats(event.getTotalSeats());
        dto.setAvailableSeats(event.getAvailableSeats());
        dto.setCategory(event.getCategory());
        dto.setImageUrl(event.getImageUrl());
        dto.setPrice(event.getPrice());
        dto.setOrganizerName(event.getOrganizerName());
        dto.setOrganizerEmail(event.getOrganizerEmail());
        dto.setStatus(event.getStatus());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        
        if (event.getCreatedBy() != null) {
            dto.setCreatedById(event.getCreatedBy().getId());
            dto.setCreatedByName(event.getCreatedBy().getName());
        }
        
        return dto;
    }
}
