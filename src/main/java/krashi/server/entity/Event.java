package krashi.server.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    
    private LocalDateTime endDateTime;
    
    private String location;
    private int totalSeats;
    private int availableSeats;
    
    private String category; // WORKSHOP, CONFERENCE, HACKATHON, MEETUP, WEBINAR, SEMINAR
    
    private String status; 
    
    private String imageUrl;
    
    private double price;
    
    private String organizerName;
    
    private String organizerEmail;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserInfo createdBy; // The admin who created this event
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

}
