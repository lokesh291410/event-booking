package krashi.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDto {
    private String title;
    private String description;
    private String date;
    private String time;
    private String location;
    private int totalSeats;
}
