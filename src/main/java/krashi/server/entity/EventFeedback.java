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
public class EventFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo user;
    
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    
    private int rating; // 1-5 stars
    private String comment;
    private String suggestions;
    private boolean wouldRecommend;
    private LocalDateTime submittedAt;
}
