package krashi.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingDto {
    private Long id;
    private String status;
    private int numberOfSeats;
    private String bookingdate;
    private String bookingtime;

    private Long eventId;
    private String eventTitle;
    private String eventDate;
    private String eventTime;
}
