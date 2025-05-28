package krashi.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingDto {
    private String status;
    private int numberOfSeats;
    private String bookingdate;
    private String bookingtime;


    private Long eventId;
    private String eventTitle;
}
