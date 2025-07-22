package krashi.server.mapping;

import krashi.server.dto.EventFeedbackDto;
import krashi.server.entity.EventFeedback;

public class EventFeedbackToDto {

    public static EventFeedbackDto mapToDto(EventFeedback feedback) {
        EventFeedbackDto dto = new EventFeedbackDto();
        dto.setEventId(feedback.getEvent().getId());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setSuggestions(feedback.getSuggestions());
        dto.setWouldRecommend(feedback.isWouldRecommend());
        return dto;
    }
}
