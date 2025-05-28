package krashi.server.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import krashi.server.dto.BookingDto;
import krashi.server.entity.Booking;
import krashi.server.entity.Event;
import krashi.server.entity.UserInfo;
import krashi.server.mapping.BookingToDto;
import krashi.server.repository.BookingRepository;
import krashi.server.repository.EventRepository;
import krashi.server.repository.UserInfoRepository;
import krashi.server.service.UserService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int MAX_SEATS_PER_BOOKING = 5;
    private final EventRepository eventRepository;
    private final UserInfoRepository userInfoRepository;
    private final BookingRepository bookingRepository;

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
        if (event.getAvailableSeats() < numberOfSeats) {
            return ResponseEntity.badRequest().body("Not enough seats available");
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
    
}
