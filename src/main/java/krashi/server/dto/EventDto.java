package krashi.server.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDto {
    @NotBlank(message = "Event title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Event date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in format yyyy-MM-dd")
    private String date;
    
    @NotBlank(message = "Event time is required")
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "Time must be in format HH:mm")
    private String time;
    
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "End date must be in format yyyy-MM-dd")
    private String endDate;
    
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "End time must be in format HH:mm")
    private String endTime;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @Min(value = 1, message = "Total seats must be at least 1")
    @Max(value = 10000, message = "Total seats cannot exceed 10000")
    private int totalSeats;
    
    @NotBlank(message = "Category is required")
    private String category; // WORKSHOP, CONFERENCE, HACKATHON, MEETUP, WEBINAR, SEMINAR
    
    private String imageUrl;
    
    @Min(value = 0, message = "Price cannot be negative")
    private double price;
    
    @NotBlank(message = "Organizer name is required")
    private String organizerName;
    
    @NotBlank(message = "Organizer email is required")
    @Email(message = "Please provide a valid email address")
    private String organizerEmail;
}
