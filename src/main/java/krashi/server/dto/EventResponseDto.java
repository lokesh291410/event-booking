package krashi.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventResponseDto {
    private Long id;
    private String title;
    private String description;
    private String date;
    private String time;
    private String endDate;
    private String endTime;
    private String location;
    private int totalSeats;
    private int availableSeats;
    private String category;
    private String imageUrl;
    private double price;
    private String organizerName;
    private String organizerEmail;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private String createdByName;
}
