package krashi.server.service;

import krashi.server.dto.PromotionResultDto;
import krashi.server.entity.Event;

public interface WaitlistPromotionService {

    PromotionResultDto processWaitlistPromotions(Event event, int availableSeats);
    boolean hasWaitlistUsers(Long eventId);
    long getWaitlistCount(Long eventId);
}