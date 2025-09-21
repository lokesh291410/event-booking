package krashi.server.service;

import krashi.server.dto.PromotionResultDto;
import krashi.server.entity.Event;

public interface WaitlistPromotionService {
    
    /**
     * Processes waitlist promotions when seats become available for an event.
     * Automatically promotes waitlisted users based on availability and first-come-first-served basis.
     * 
     * @param event The event for which seats have become available
     * @param availableSeats Number of seats that became available
     * @return PromotionResultDto containing details of promotions made
     */
    PromotionResultDto processWaitlistPromotions(Event event, int availableSeats);
    
    /**
     * Checks if there are users on waitlist for a specific event.
     * 
     * @param eventId The ID of the event to check
     * @return true if there are users waiting, false otherwise
     */
    boolean hasWaitlistUsers(Long eventId);
    
    /**
     * Gets the count of users waiting for a specific event.
     * 
     * @param eventId The ID of the event
     * @return Number of users on waitlist
     */
    long getWaitlistCount(Long eventId);
}