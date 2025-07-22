package krashi.server.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import krashi.server.dto.BookingDto;
import krashi.server.dto.EventFeedbackDto;
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
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        if(numberOfSeats <= 0 || numberOfSeats > MAX_SEATS_PER_BOOKING) {
            return ResponseEntity.badRequest().body("Invalid number of seats requested");
        }
        
        if (!event.getStatus().equals("PUBLISHED")) {
            return ResponseEntity.badRequest().body("Event is not available for booking");
        }
        
        if (event.getAvailableSeats() < numberOfSeats) {
            return ResponseEntity.badRequest().body("Not enough seats available. Would you like to join the waitlist?");
        }

        UserInfo user = userInfoRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDateTime(java.time.LocalDateTime.now());
        booking.setStatus("Confirmed");

        event.setAvailableSeats(event.getAvailableSeats() - numberOfSeats);
        eventRepository.save(event);
        bookingRepository.save(booking);

        return ResponseEntity.ok("Booking successful");
    }

    @Override
    public ResponseEntity<?> cancelBooking(Long bookingId, Long userId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = optionalBooking.get();
        if (!booking.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("You are not authorized to cancel this booking");
        }

        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfSeats());
        eventRepository.save(event);

        booking.setStatus("Cancelled");
        bookingRepository.save(booking);
        
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @Override
    public ResponseEntity<?> getBookingDetails(Long bookingId, Long userId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = optionalBooking.get();
        if (!booking.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("You are not authorized to view this booking");
        }

        BookingDto bookingDto = BookingToDto.mapToDto(booking);

        return ResponseEntity.ok(bookingDto);
    }

    @Override
    public ResponseEntity<?> getUserBookings(Long userId) {
        Optional<UserInfo> optionalUser = userInfoRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Booking> bookings = bookingRepository.findByUser_Id(userId);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingToDto::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingDtos);
    }

    @Override
    public ResponseEntity<?> joinWaitlist(Long eventId, Long userId, int numberOfSeats) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        if (event.getAvailableSeats() >= numberOfSeats) {
            return ResponseEntity.badRequest().body("Event has available seats. Please book directly.");
        }

        if (waitlistRepository.existsByUserIdAndEventId(userId, eventId)) {
            return ResponseEntity.badRequest().body("You are already on the waitlist for this event");
        }

        UserInfo user = userInfoRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

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
        List<Waitlist> waitlist = waitlistRepository.findByUserIdAndStatus(userId, "WAITING");
        return ResponseEntity.ok(waitlist);
    }

    @Override
    public ResponseEntity<?> removeFromWaitlist(Long waitlistId, Long userId) {
        Optional<Waitlist> optionalWaitlist = waitlistRepository.findById(waitlistId);
        if (optionalWaitlist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Waitlist waitlist = optionalWaitlist.get();
        if (!waitlist.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        waitlistRepository.delete(waitlist);
        return ResponseEntity.ok("Removed from waitlist");
    }

    @Override
    public ResponseEntity<?> submitEventFeedback(EventFeedbackDto feedbackDto, Long userId) {
        if (eventFeedbackRepository.existsByUserIdAndEventId(userId, feedbackDto.getEventId())) {
            return ResponseEntity.badRequest().body("You have already submitted feedback for this event");
        }

        UserInfo user = userInfoRepository.findById(userId).orElse(null);
        Event event = eventRepository.findById(feedbackDto.getEventId()).orElse(null);
        
        if (user == null || event == null) {
            return ResponseEntity.notFound().build();
        }

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
        List<Event> events = eventRepository.findByCategory(category);
        return ResponseEntity.ok(events);
    }

    @Override
    public ResponseEntity<?> searchEvents(String keyword) {
        List<Event> events = eventRepository.findByTitleContainingIgnoreCase(keyword);
        return ResponseEntity.ok(events);
    }
    
}
