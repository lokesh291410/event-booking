package krashi.server.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import krashi.server.dto.BookingDto;
import krashi.server.dto.EventFeedbackDto;
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
import krashi.server.repository.BookingRepository;
import krashi.server.repository.EventFeedbackRepository;
import krashi.server.repository.EventRepository;
import krashi.server.repository.UserInfoRepository;
import krashi.server.repository.WaitlistRepository;
import krashi.server.service.UserService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int MAX_SEATS_PER_BOOKING = 5;
    private final EventRepository eventRepository;
    private final UserInfoRepository userInfoRepository;
    private final BookingRepository bookingRepository;
    private final WaitlistRepository waitlistRepository;
    private final EventFeedbackRepository eventFeedbackRepository;

    @Override
    public ResponseEntity<?> bookEvent(Long eventId, Long userId, int numberOfSeats) {
        // Validate input parameters
        if (eventId == null || userId == null) {
            throw new BadRequestException("Event ID and User ID are required");
        }
        
        if (numberOfSeats <= 0 || numberOfSeats > MAX_SEATS_PER_BOOKING) {
            throw new BadRequestException("Number of seats must be between 1 and " + MAX_SEATS_PER_BOOKING);
        }

        // Find event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found"));
        
        // Check if event is published
        if (!"PUBLISHED".equals(event.getStatus())) {
            throw new BadRequestException("Event is not available for booking");
        }
        
        // Check seat availability
        if (event.getAvailableSeats() < numberOfSeats) {
            throw new InsufficientSeatsException("Not enough seats available. Available: " + event.getAvailableSeats() + ", Requested: " + numberOfSeats);
        }

        // Find user
        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setStatus("Confirmed");

        // Update available seats
        event.setAvailableSeats(event.getAvailableSeats() - numberOfSeats);
        eventRepository.save(event);
        bookingRepository.save(booking);

        return ResponseEntity.ok("Booking successful");
    }

    @Override
    public ResponseEntity<?> cancelBooking(Long bookingId, Long userId) {
        // Validate input parameters
        if (bookingId == null || userId == null) {
            throw new BadRequestException("Booking ID and User ID are required");
        }

        // Find booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with ID " + bookingId + " not found"));

        // Check authorization
        if (!booking.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to cancel this booking");
        }

        // Check if booking can be cancelled
        if ("Cancelled".equals(booking.getStatus())) {
            throw new BadRequestException("Booking is already cancelled");
        }

        // Update event seats
        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfSeats());
        eventRepository.save(event);

        // Cancel booking
        booking.setStatus("Cancelled");
        bookingRepository.save(booking);
        
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @Override
    public ResponseEntity<?> getBookingDetails(Long bookingId, Long userId) {
        // Validate input parameters
        if (bookingId == null || userId == null) {
            throw new BadRequestException("Booking ID and User ID are required");
        }

        // Find booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with ID " + bookingId + " not found"));

        // Check authorization
        if (!booking.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to view this booking");
        }

        BookingDto bookingDto = BookingToDto.mapToDto(booking);
        return ResponseEntity.ok(bookingDto);
    }

    @Override
    public ResponseEntity<?> getUserBookings(Long userId) {
        // Validate input parameters
        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }

        // Verify user exists
        userInfoRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        // Get user bookings
        List<Booking> bookings = bookingRepository.findByUser_Id(userId);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingToDto::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingDtos);
    }

    @Override
    public ResponseEntity<?> joinWaitlist(Long eventId, Long userId, int numberOfSeats) {
        // Validate input parameters
        if (eventId == null || userId == null) {
            throw new BadRequestException("Event ID and User ID are required");
        }
        
        if (numberOfSeats <= 0) {
            throw new BadRequestException("Number of seats must be greater than 0");
        }

        // Find event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + eventId + " not found"));

        // Check if event has available seats
        if (event.getAvailableSeats() >= numberOfSeats) {
            throw new BadRequestException("Event has available seats. Please book directly.");
        }

        // Check if user is already on waitlist
        if (waitlistRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new BadRequestException("You are already on the waitlist for this event");
        }

        // Find user
        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        // Create waitlist entry
        Waitlist waitlist = new Waitlist();
        waitlist.setUser(user);
        waitlist.setEvent(event);
        waitlist.setRequestedSeats(numberOfSeats);
        waitlist.setStatus("WAITING");
        waitlist.setJoinedAt(LocalDateTime.now());

        waitlistRepository.save(waitlist);
        return ResponseEntity.ok("Successfully joined waitlist");
    }

    @Override
    public ResponseEntity<?> getUserWaitlist(Long userId) {
        // Validate input parameters
        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }

        // Verify user exists
        userInfoRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        // Get user waitlist entries
        List<Waitlist> waitlist = waitlistRepository.findByUserIdAndStatus(userId, "WAITING");
        return ResponseEntity.ok(waitlist);
    }

    @Override
    public ResponseEntity<?> removeFromWaitlist(Long waitlistId, Long userId) {
        // Validate input parameters
        if (waitlistId == null || userId == null) {
            throw new BadRequestException("Waitlist ID and User ID are required");
        }

        // Find waitlist entry
        Waitlist waitlist = waitlistRepository.findById(waitlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Waitlist entry with ID " + waitlistId + " not found"));

        // Check authorization
        if (!waitlist.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to remove this waitlist entry");
        }

        waitlistRepository.delete(waitlist);
        return ResponseEntity.ok("Removed from waitlist");
    }

    @Override
    public ResponseEntity<?> submitEventFeedback(EventFeedbackDto feedbackDto, Long userId) {
        // Validate input parameters
        if (feedbackDto == null || userId == null) {
            throw new BadRequestException("Feedback data and User ID are required");
        }
        
        if (feedbackDto.getEventId() == null) {
            throw new BadRequestException("Event ID is required in feedback");
        }
        
        if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        // Check if feedback already exists
        if (eventFeedbackRepository.existsByUserIdAndEventId(userId, feedbackDto.getEventId())) {
            throw new BadRequestException("You have already submitted feedback for this event");
        }

        // Find user and event
        UserInfo user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        
        Event event = eventRepository.findById(feedbackDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event with ID " + feedbackDto.getEventId() + " not found"));

        // Create feedback
        EventFeedback feedback = new EventFeedback();
        feedback.setUser(user);
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
    public ResponseEntity<?> getUserFeedback(Long userId) {
        // Validate input parameters
        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }

        // Verify user exists
        userInfoRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        // Get user feedback
        List<EventFeedback> feedback = eventFeedbackRepository.findByUserId(userId);
        return ResponseEntity.ok(feedback);
    }

    @Override
    public ResponseEntity<?> getUpcomingEvents() {
        List<Event> events = eventRepository.findUpcomingPublicEvents(LocalDateTime.now());
        return ResponseEntity.ok(events);
    }

    @Override
    public ResponseEntity<?> getEventsByCategory(String category) {
        // Validate input parameters
        if (category == null || category.trim().isEmpty()) {
            throw new BadRequestException("Category is required");
        }

        List<Event> events = eventRepository.findByCategory(category);
        return ResponseEntity.ok(events);
    }

    @Override
    public ResponseEntity<?> searchEvents(String keyword) {
        List<Event> events = eventRepository.findByTitleContainingIgnoreCase(keyword);
        return ResponseEntity.ok(events);
    }
    
}
