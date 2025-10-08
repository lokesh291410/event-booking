package krashi.server.service;

import krashi.server.dto.PromotedUserDto;
import krashi.server.entity.UserInfo;
import krashi.server.entity.Event;
import java.util.List;

public interface EmailNotificationService {

    void sendBookingConfirmationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId);

    void sendWaitlistConfirmationEmail(UserInfo user, Event event, int requestedSeats);

    void sendPromotionNotificationEmails(List<PromotedUserDto> promotedUsers);

    void sendPromotionNotificationEmail(PromotedUserDto promotedUser);

    void sendBookingCancellationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId);

    void sendNotificationEmail(String userEmail, String subject, String message);
}