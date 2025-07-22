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
    private String endDate;
    private String endTime;
    private String location;
    private int totalSeats;
    private String category; // WORKSHOP, CONFERENCE, HACKATHON, MEETUP, WEBINAR, SEMINAR
    private String imageUrl;
    private double price;
    private String organizerName;
    private String organizerEmail;
}
