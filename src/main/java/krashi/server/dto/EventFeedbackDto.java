package krashi.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventFeedbackDto {
    private Long eventId;
    private int rating;
    private String comment;
    private String suggestions;
    private boolean wouldRecommend;
}
