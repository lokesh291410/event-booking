package krashi.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserEventDetailDto {
    // Basic event information
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
    
    // User-relevant statistics
    private int bookedSeats;
    private boolean available;
    private boolean hasWaitlist;
    private int waitlistCount;
    private double averageRating;
    private int totalFeedbacks;
    
    // Recent public feedbacks (for user reference)
    private List<PublicFeedbackDto> recentFeedbacks;
    
    // Inner class for public feedback display
    @Getter
    @Setter
    public static class PublicFeedbackDto {
        private String userName;
        private int rating;
        private String comment;
        private boolean wouldRecommend;
        private LocalDateTime submittedAt;
    }
}
