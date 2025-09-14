package krashi.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserEventDetailDto {
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
    
    private int bookedSeats;
    private boolean available;
    private boolean hasWaitlist;
    private int waitlistCount;
    private double averageRating;
    private int totalFeedbacks;
    
    private List<PublicFeedbackDto> recentFeedbacks;
    
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
