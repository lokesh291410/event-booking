package krashi.server.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import krashi.server.dto.BookingDto;
import krashi.server.dto.EventFeedbackDto;
import krashi.server.dto.EventResponseDto;
import krashi.server.dto.UserFeedbackResponseDto;
import krashi.server.exception.AccessDeniedException;
import krashi.server.exception.BadRequestException;
import krashi.server.exception.InsufficientSeatsException;
import krashi.server.exception.ResourceNotFoundException;
import krashi.server.entity.Booking;
import krashi.server.entity.Event;
import krashi.server.entity.EventFeedback;
import krashi.server.entity.UserInfo;
import krashi.server.entity.Waitlist;
import krashi.server.mapping.BookingToDto;
import krashi.server.mapping.EventFeedbackToDto;
import krashi.server.mapping.EventToDto;
import krashi.server.repository.BookingRepository;
import krashi.server.repository.EventFeedbackRepository;
import krashi.server.repository.EventRepository;
import krashi.server.repository.WaitlistRepository;
import krashi.server.service.AuthenticationService;
import krashi.server.service.UserService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int MAX_SEATS_PER_BOOKING = 5;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final WaitlistRepository waitlistRepository;
    private final EventFeedbackRepository eventFeedbackRepository;
    private final AuthenticationService authenticationService;

    private void verifyBookingOwnership(Booking booking, UserInfo user) {
        if (booking.getUser() == null) {
            throw new BadRequestException("Booking has no user assigned");
        }
        
        if (user == null || user.getId() == null) {
            throw new BadRequestException("Invalid user");
        }
        
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only access your own bookings");
        }
    }

    private void verifyWaitlistOwnership(Waitlist waitlist, UserInfo user) {
        if (waitlist.getUser() == null) {
            throw new BadRequestException("Waitlist entry has no user assigned");
        }
        
        if (user == null || user.getId() == null) {
            throw new BadRequestException("Invalid user");
        }
        
        if (!waitlist.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only access your own waitlist entries");
        }
    }

    private void verifyFeedbackEligibility(Long eventId, UserInfo user) {
        List<Booking> userBookings = bookingRepository.findByUser_Id(user.getId());
        boolean hasConfirmedBooking = userBookings.stream()
            .anyMatch(booking -> booking.getEvent().getId().equals(eventId) && 
                                "Confirmed".equals(booking.getStatus()));
        
        if (!hasConfirmedBooking) {
            throw new AccessDeniedException("You can only submit feedback for events you have booked");
        }
    }

    @Override
    public ResponseEntity<?> bookEvent(Long eventId, int numberOfSeats) {
        UserInfo user = authenticationService.getCurrentUser();
        
        if (eventId == null) {
            throw new BadRequestException("Event ID is required");
        }
        
        if (numberOfSeats <= 0 || numberOfSeats > MAX_SEATS_PER_BOOKING) {
            throw new BadRequestException("Number of seats must be between 1 and " + MAX_SEATS_PER_BOOKING);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        if (!"PUBLISHED".equals(event.getStatus())) {
            throw new BadRequestException("Event is not available for booking");
        }
        
        if (event.getAvailableSeats() < numberOfSeats) {
            throw new InsufficientSeatsException("Not enough seats available. Available: " + event.getAvailableSeats() + ", Requested: " + numberOfSeats);
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setStatus("Confirmed");

        event.setAvailableSeats(event.getAvailableSeats() - numberOfSeats);
        eventRepository.save(event);
        bookingRepository.save(booking);

        return ResponseEntity.ok("Booking successful");
    }

    @Override
    public ResponseEntity<?> cancelBooking(Long bookingId) {
        UserInfo currentUser = authenticationService.getCurrentUser();
        
        if (bookingId == null) {
            throw new BadRequestException("Booking ID is required");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        verifyBookingOwnership(booking, currentUser);

        if ("Cancelled".equals(booking.getStatus())) {
            throw new BadRequestException("Booking is already cancelled");
        }

        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfSeats());
        eventRepository.save(event);

        booking.setStatus("Cancelled");
        bookingRepository.save(booking);
        
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @Override
    public ResponseEntity<?> getBookingDetails(Long bookingId) {
        UserInfo currentUser = authenticationService.getCurrentUser();
        
        if (bookingId == null) {
            throw new BadRequestException("Booking ID is required");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        verifyBookingOwnership(booking, currentUser);

        BookingDto bookingDto = BookingToDto.mapToDto(booking);
        return ResponseEntity.ok(bookingDto);
    }

    @Override
    public ResponseEntity<?> getUserBookings() {
        UserInfo currentUser = authenticationService.getCurrentUser();
        
        List<Booking> bookings = bookingRepository.findByUser_Id(currentUser.getId());
        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingToDto::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingDtos);
    }

    @Override
    public ResponseEntity<?> joinWaitlist(Long eventId, int numberOfSeats) {
        UserInfo currentUser = authenticationService.getCurrentUser();
        
        if (eventId == null) {
            throw new BadRequestException("Event ID is required");
        }
        
        if (numberOfSeats <= 0) {
            throw new BadRequestException("Number of seats must be greater than 0");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (event.getAvailableSeats() >= numberOfSeats) {
            throw new BadRequestException("Event has available seats. Please book directly.");
        }

        if (waitlistRepository.existsByUserIdAndEventId(currentUser.getId(), eventId)) {
            throw new BadRequestException("You are already on the waitlist for this event");
        }

        Waitlist waitlist = new Waitlist();
        waitlist.setUser(currentUser);
        waitlist.setEvent(event);
        waitlist.setRequestedSeats(numberOfSeats);
        waitlist.setStatus("WAITING");
        waitlist.setJoinedAt(LocalDateTime.now());

        waitlistRepository.save(waitlist);
        return ResponseEntity.ok("Successfully joined waitlist");
    }

    @Override
    public ResponseEntity<?> getUserWaitlist() {
        UserInfo currentUser = authenticationService.getCurrentUser();
        
        List<Waitlist> waitlist = waitlistRepository.findByUserIdAndStatus(currentUser.getId(), "WAITING");
        return ResponseEntity.ok(waitlist);
    }

    @Override
    public ResponseEntity<?> removeFromWaitlist(Long waitlistId) {
        UserInfo currentUser = authenticationService.getCurrentUser();
        
        if (waitlistId == null) {
            throw new BadRequestException("Waitlist ID is required");
        }

        Waitlist waitlist = waitlistRepository.findById(waitlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Waitlist entry", "id", waitlistId));

        verifyWaitlistOwnership(waitlist, currentUser);

        waitlistRepository.delete(waitlist);
        return ResponseEntity.ok("Removed from waitlist");
    }

    @Override
    public ResponseEntity<?> submitEventFeedback(EventFeedbackDto feedbackDto) {
        UserInfo currentUser = authenticationService.getCurrentUser();
        
        if (feedbackDto == null) {
            throw new BadRequestException("Feedback data is required");
        }
        
        if (feedbackDto.getEventId() == null) {
            throw new BadRequestException("Event ID is required in feedback");
        }
        
        if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        // Verify user has booked this event
        verifyFeedbackEligibility(feedbackDto.getEventId(), currentUser);

        if (eventFeedbackRepository.existsByUserIdAndEventId(currentUser.getId(), feedbackDto.getEventId())) {
            throw new BadRequestException("You have already submitted feedback for this event");
        }
        
        Event event = eventRepository.findById(feedbackDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", feedbackDto.getEventId()));

        EventFeedback feedback = new EventFeedback();
        feedback.setUser(currentUser);
        feedback.setEvent(event);
        feedback.setRating(feedbackDto.getRating());
        feedback.setComment(feedbackDto.getComment());
        feedback.setSuggestions(feedbackDto.getSuggestions());
        feedback.setWouldRecommend(feedbackDto.isWouldRecommend());
        feedback.setSubmittedAt(LocalDateTime.now());

        eventFeedbackRepository.save(feedback);
        return ResponseEntity.ok("Feedback submitted successfully");
    }

    @Override
    public ResponseEntity<?> getUserFeedback() {
        UserInfo currentUser = authenticationService.getCurrentUser();
        
        List<EventFeedback> feedback = eventFeedbackRepository.findByUserId(currentUser.getId());
        List<UserFeedbackResponseDto> feedbackDtos = feedback.stream()
                .map(EventFeedbackToDto::mapToUserResponseDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(feedbackDtos);
    }

    @Override
    public ResponseEntity<?> getUpcomingEvents() {
        List<Event> events = eventRepository.findUpcomingPublicEvents(LocalDateTime.now());
        List<EventResponseDto> eventDtos = events.stream()
                .map(EventToDto::mapToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }

    @Override
    public ResponseEntity<?> getEventsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new BadRequestException("Category is required");
        }

        List<Event> events = eventRepository.findByCategoryAndStatus(category, "PUBLISHED");
        List<EventResponseDto> eventDtos = events.stream()
                .map(EventToDto::mapToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }

    @Override
    public ResponseEntity<?> searchEvents(String keyword) {
        List<Event> events = eventRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, "PUBLISHED");
        List<EventResponseDto> eventDtos = events.stream()
                .map(EventToDto::mapToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }
    
}
