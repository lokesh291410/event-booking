package krashi.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserFeedbackResponseDto {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private int rating;
    private String comment;
    private String suggestions;
    private boolean wouldRecommend;
    private LocalDateTime submittedAt;
}
