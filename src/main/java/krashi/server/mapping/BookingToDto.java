package krashi.server.mapping;

import krashi.server.dto.BookingDto;
import krashi.server.entity.Booking;

import java.time.format.DateTimeFormatter;

public class BookingToDto {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static BookingDto mapToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setNumberOfSeats(booking.getNumberOfSeats());
        bookingDto.setBookingdate(booking.getBookingDateTime().toLocalDate().toString());
        bookingDto.setBookingtime(booking.getBookingDateTime().toLocalTime().toString());
        
        if (booking.getEvent() != null) {
            bookingDto.setEventId(booking.getEvent().getId());
            bookingDto.setEventTitle(booking.getEvent().getTitle());
            
            // Add event date and time
            if (booking.getEvent().getDateTime() != null) {
                bookingDto.setEventDate(booking.getEvent().getDateTime().toLocalDate().format(DATE_FORMATTER));
                bookingDto.setEventTime(booking.getEvent().getDateTime().toLocalTime().format(TIME_FORMATTER));
            }
        }
        
        return bookingDto;
    }
}
