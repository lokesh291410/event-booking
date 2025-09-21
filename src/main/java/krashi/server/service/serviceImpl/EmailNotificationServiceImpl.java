package krashi.server.service.serviceImpl;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import krashi.server.dto.PromotedUserDto;
import krashi.server.entity.Event;
import krashi.server.entity.UserInfo;
import krashi.server.service.EmailNotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final JavaMailSender javaMailSender;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @Override
    public void sendBookingConfirmationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId) {
        String subject = "Booking Confirmation - " + event.getTitle();
        String message = buildBookingConfirmationMessage(user, event, numberOfSeats, bookingId);
        
        sendEmail(user.getEmail(), subject, message);
        log.info("Booking confirmation email sent to: {} for event: {}", user.getEmail(), event.getTitle());
    }

    @Override
    public void sendWaitlistConfirmationEmail(UserInfo user, Event event, int requestedSeats) {
        String subject = "Waitlist Confirmation - " + event.getTitle();
        String message = buildWaitlistConfirmationMessage(user, event, requestedSeats);
        
        sendEmail(user.getEmail(), subject, message);
        log.info("Waitlist confirmation email sent to: {} for event: {}", user.getEmail(), event.getTitle());
    }

    @Override
    public void sendPromotionNotificationEmails(List<PromotedUserDto> promotedUsers) {
        for (PromotedUserDto promotedUser : promotedUsers) {
            sendPromotionNotificationEmail(promotedUser);
        }
        log.info("Promotion notification emails sent to {} users", promotedUsers.size());
    }

    @Override
    public void sendPromotionNotificationEmail(PromotedUserDto promotedUser) {
        String subject = "Great News! You've been confirmed for " + promotedUser.getEventTitle();
        String message = buildPromotionNotificationMessage(promotedUser);
        
        sendEmail(promotedUser.getUserEmail(), subject, message);
        log.info("Promotion notification email sent to: {} for event: {}", 
                promotedUser.getUserEmail(), promotedUser.getEventTitle());
    }

    @Override
    public void sendBookingCancellationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId) {
        String subject = "Booking Cancellation Confirmation - " + event.getTitle();
        String message = buildCancellationConfirmationMessage(user, event, numberOfSeats, bookingId);
        
        sendEmail(user.getEmail(), subject, message);
        log.info("Booking cancellation email sent to: {} for event: {}", user.getEmail(), event.getTitle());
    }

    @Override
    public void sendNotificationEmail(String userEmail, String subject, String message) {
        sendEmail(userEmail, subject, message);
        log.info("General notification email sent to: {}", userEmail);
    }

    private void sendEmail(String toEmail, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            
            javaMailSender.send(mailMessage);
            log.info("Email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", toEmail, e.getMessage());
        }
    }

    private String buildBookingConfirmationMessage(UserInfo user, Event event, int numberOfSeats, Long bookingId) {
        return String.format(
            "Dear %s,\n\n" +
            "Your booking has been confirmed!\n\n" +
            "Event Details:\n" +
            "- Event: %s\n" +
            "- Date: %s\n" +
            "- Time: %s\n" +
            "- Location: %s\n" +
            "- Number of Seats: %d\n" +
            "- Booking ID: %d\n\n" +
            "We look forward to seeing you at the event!\n\n" +
            "Best regards,\n" +
            "Krashi Events Team",
            user.getName(),
            event.getTitle(),
            event.getDateTime().format(DATE_FORMATTER),
            event.getDateTime().format(TIME_FORMATTER),
            event.getLocation(),
            numberOfSeats,
            bookingId
        );
    }

    private String buildWaitlistConfirmationMessage(UserInfo user, Event event, int requestedSeats) {
        return String.format(
            "Dear %s,\n\n" +
            "You have been added to the waitlist for the following event:\n\n" +
            "Event Details:\n" +
            "- Event: %s\n" +
            "- Date: %s\n" +
            "- Time: %s\n" +
            "- Location: %s\n" +
            "- Requested Seats: %d\n\n" +
            "The event is currently full, but don't worry! You're now on our waitlist and will be automatically notified if seats become available.\n\n" +
            "Best regards,\n" +
            "Krashi Events Team",
            user.getName(),
            event.getTitle(),
            event.getDateTime().format(DATE_FORMATTER),
            event.getDateTime().format(TIME_FORMATTER),
            event.getLocation(),
            requestedSeats
        );
    }

    private String buildPromotionNotificationMessage(PromotedUserDto promotedUser) {
        return String.format(
            "Dear %s,\n\n" +
            "Excellent news! A seat has become available and you've been automatically moved from the waitlist to confirmed booking!\n\n" +
            "Event Details:\n" +
            "- Event: %s\n" +
            "- Confirmed Seats: %d\n" +
            "- Booking ID: %d\n\n" +
            "Your booking is now confirmed and you're all set to attend the event.\n\n" +
            "Best regards,\n" +
            "Krashi Events Team",
            promotedUser.getUserName(),
            promotedUser.getEventTitle(),
            promotedUser.getSeatsPromoted(),
            promotedUser.getNewBookingId()
        );
    }

    private String buildCancellationConfirmationMessage(UserInfo user, Event event, int numberOfSeats, Long bookingId) {
        return String.format(
            "Dear %s,\n\n" +
            "Your booking cancellation has been confirmed.\n\n" +
            "Cancelled Booking Details:\n" +
            "- Event: %s\n" +
            "- Date: %s\n" +
            "- Time: %s\n" +
            "- Location: %s\n" +
            "- Cancelled Seats: %d\n" +
            "- Booking ID: %d\n\n" +
            "Thank you for using Krashi Events.\n\n" +
            "Best regards,\n" +
            "Krashi Events Team",
            user.getName(),
            event.getTitle(),
            event.getDateTime().format(DATE_FORMATTER),
            event.getDateTime().format(TIME_FORMATTER),
            event.getLocation(),
            numberOfSeats,
            bookingId
        );
    }
}