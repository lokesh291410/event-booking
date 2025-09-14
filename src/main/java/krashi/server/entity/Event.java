package krashi.server.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    
    private String status; // SCHEDULED, CANCELLED, COMPLETED, DRAFT
    
    private String imageUrl;
    
    private double price;
    
    private String organizerName;
    
    private String organizerEmail;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserInfo createdBy;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> booking;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Waitlist> waitlists;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventFeedback> feedbacks;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

}
