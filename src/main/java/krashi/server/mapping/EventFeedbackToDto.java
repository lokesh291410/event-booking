package krashi.server.mapping;

import krashi.server.dto.EventFeedbackDto;
import krashi.server.dto.UserFeedbackResponseDto;
import krashi.server.entity.EventFeedback;

import java.time.format.DateTimeFormatter;

public class EventFeedbackToDto {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static EventFeedbackDto mapToDto(EventFeedback feedback) {
        EventFeedbackDto dto = new EventFeedbackDto();
        dto.setEventId(feedback.getEvent().getId());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setSuggestions(feedback.getSuggestions());
        dto.setWouldRecommend(feedback.isWouldRecommend());
        return dto;
    }
    
    public static UserFeedbackResponseDto mapToUserResponseDto(EventFeedback feedback) {
        UserFeedbackResponseDto dto = new UserFeedbackResponseDto();
        
        dto.setId(feedback.getId());
        dto.setEventId(feedback.getEvent().getId());
        dto.setEventTitle(feedback.getEvent().getTitle());
        
        if (feedback.getEvent().getDateTime() != null) {
            dto.setEventDate(feedback.getEvent().getDateTime().format(DATE_FORMATTER));
        }
        
        dto.setEventLocation(feedback.getEvent().getLocation());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setSuggestions(feedback.getSuggestions());
        dto.setWouldRecommend(feedback.isWouldRecommend());
        dto.setSubmittedAt(feedback.getSubmittedAt());
        
        return dto;
    }
}
