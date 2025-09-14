package krashi.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventDetailResponseDto {
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
    
    private int totalBookings;
    private int bookedSeats;
    private int waitlistCount;
    private double averageRating;
    private int totalFeedbacks;
    
    private List<BookingDto> recentBookings;
    
    private List<WaitlistSummaryDto> waitlistUsers;
    
    private List<FeedbackSummaryDto> recentFeedbacks;
    
    @Getter
    @Setter
    public static class WaitlistSummaryDto {
        private Long id;
        private String userName;
        private String userEmail;
        private int requestedSeats;
        private String status;
        private LocalDateTime joinedAt;
        private LocalDateTime notifiedAt;
    }
    
    @Getter
    @Setter
    public static class FeedbackSummaryDto {
        private Long id;
        private String userName;
        private int rating;
        private String comment;
        private boolean wouldRecommend;
        private LocalDateTime submittedAt;
    }
}
