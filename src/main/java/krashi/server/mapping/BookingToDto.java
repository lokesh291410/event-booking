package krashi.server.mapping;

import krashi.server.dto.BookingDto;
import krashi.server.entity.Booking;

public class BookingToDto {

    public static BookingDto mapToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setNumberOfSeats(booking.getNumberOfSeats());
        bookingDto.setBookingdate(booking.getBookingDateTime().toLocalDate().toString());
        bookingDto.setBookingtime(booking.getBookingDateTime().toLocalTime().toString());
        
        if (booking.getEvent() != null) {
            bookingDto.setEventId(booking.getEvent().getId());
            bookingDto.setEventTitle(booking.getEvent().getTitle());
        }
        
        return bookingDto;
    }
    
}
