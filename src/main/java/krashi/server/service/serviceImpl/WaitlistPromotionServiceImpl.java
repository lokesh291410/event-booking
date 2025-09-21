package krashi.server.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import krashi.server.dto.PromotedUserDto;
import krashi.server.dto.PromotionResultDto;
import krashi.server.entity.Booking;
import krashi.server.entity.Event;
import krashi.server.entity.Waitlist;
import krashi.server.repository.BookingRepository;
import krashi.server.repository.EventRepository;
import krashi.server.repository.WaitlistRepository;
import krashi.server.service.WaitlistPromotionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WaitlistPromotionServiceImpl implements WaitlistPromotionService {

    private final WaitlistRepository waitlistRepository;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    @Override
    public PromotionResultDto processWaitlistPromotions(Event event, int availableSeats) {
        log.info("Processing waitlist promotions for event: {} with {} available seats", 
                event.getId(), availableSeats);
        
        // Get all users waiting for this event, sorted by join date (first-come-first-served)
        List<Waitlist> waitingUsers = waitlistRepository.findByEventIdAndStatus(event.getId(), "WAITING");
        
        if (waitingUsers.isEmpty()) {
            log.info("No users on waitlist for event: {}", event.getId());
            return createEmptyPromotionResult();
        }
        
        // Sort by join date to ensure fairness
        waitingUsers.sort((w1, w2) -> w1.getJoinedAt().compareTo(w2.getJoinedAt()));
        
        List<PromotedUserDto> promotedUsers = new ArrayList<>();
        int remainingSeats = availableSeats;
        int totalPromotedUsers = 0;
        int totalSeatsPromoted = 0;
        
        for (Waitlist waitlistEntry : waitingUsers) {
            if (remainingSeats >= waitlistEntry.getRequestedSeats()) {
                // Promote this user from waitlist to confirmed booking
                Booking newBooking = createBookingFromWaitlist(waitlistEntry);
                Booking savedBooking = bookingRepository.save(newBooking);
                
                // Update remaining seats
                remainingSeats -= waitlistEntry.getRequestedSeats();
                totalSeatsPromoted += waitlistEntry.getRequestedSeats();
                totalPromotedUsers++;
                
                // Create promoted user DTO
                PromotedUserDto promotedUser = createPromotedUserDto(waitlistEntry, savedBooking);
                promotedUsers.add(promotedUser);
                
                // Remove from waitlist
                waitlistRepository.delete(waitlistEntry);
                
                log.info("Promoted user {} for event {} - {} seats", 
                        waitlistEntry.getUser().getUserName(), 
                        event.getId(), 
                        waitlistEntry.getRequestedSeats());
            } else {
                // No more seats available for remaining waitlist users
                log.info("Insufficient seats for remaining waitlist users. Remaining: {}, Required: {}", 
                        remainingSeats, waitlistEntry.getRequestedSeats());
                break;
            }
        }
        
        // Update event with final available seats count
        event.setAvailableSeats(remainingSeats);
        eventRepository.save(event);
        
        return createPromotionResult(promotedUsers, totalPromotedUsers, totalSeatsPromoted);
    }
    
    @Override
    public boolean hasWaitlistUsers(Long eventId) {
        return waitlistRepository.countByEvent_IdAndStatus(eventId, "WAITING") > 0;
    }
    
    @Override
    public long getWaitlistCount(Long eventId) {
        return waitlistRepository.countByEvent_IdAndStatus(eventId, "WAITING");
    }
    
    private Booking createBookingFromWaitlist(Waitlist waitlistEntry) {
        Booking booking = new Booking();
        booking.setUser(waitlistEntry.getUser());
        booking.setEvent(waitlistEntry.getEvent());
        booking.setNumberOfSeats(waitlistEntry.getRequestedSeats());
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setStatus("Confirmed");
        return booking;
    }
    
    private PromotedUserDto createPromotedUserDto(Waitlist waitlistEntry, Booking newBooking) {
        return new PromotedUserDto(
                waitlistEntry.getUser().getId(),
                waitlistEntry.getUser().getUserName(),
                waitlistEntry.getUser().getEmail(),
                waitlistEntry.getEvent().getId(),
                waitlistEntry.getEvent().getTitle(),
                waitlistEntry.getRequestedSeats(),
                newBooking.getId()
        );
    }
    
    private PromotionResultDto createEmptyPromotionResult() {
        return new PromotionResultDto(
                false, 
                0, 
                0, 
                new ArrayList<>(), 
                "No users were promoted from waitlist"
        );
    }
    
    private PromotionResultDto createPromotionResult(List<PromotedUserDto> promotedUsers, 
                                                   int totalPromotedUsers, 
                                                   int totalSeatsPromoted) {
        StringBuilder message = new StringBuilder();
        message.append("Successfully promoted ").append(totalPromotedUsers)
               .append(" user(s) from waitlist for a total of ").append(totalSeatsPromoted)
               .append(" seat(s). Promoted users: ");
        
        for (int i = 0; i < promotedUsers.size(); i++) {
            PromotedUserDto user = promotedUsers.get(i);
            if (i > 0) message.append(", ");
            message.append(user.getUserName()).append(" (").append(user.getSeatsPromoted()).append(" seats)");
        }
        
        return new PromotionResultDto(
                true, 
                totalPromotedUsers, 
                totalSeatsPromoted, 
                promotedUsers, 
                message.toString()
        );
    }
}