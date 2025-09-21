package krashi.server.service;

import krashi.server.dto.PromotedUserDto;
import krashi.server.entity.UserInfo;
import krashi.server.entity.Event;
import java.util.List;

public interface EmailNotificationService {
    
    /**
     * Sends booking confirmation email to a user.
     * 
     * @param user The user to send the email to
     * @param event The event that was booked
     * @param numberOfSeats Number of seats booked
     * @param bookingId The booking ID for reference
     */
    void sendBookingConfirmationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId);
    
    /**
     * Sends waitlist confirmation email to a user.
     * 
     * @param user The user to send the email to
     * @param event The event they were added to waitlist for
     * @param requestedSeats Number of seats requested
     */
    void sendWaitlistConfirmationEmail(UserInfo user, Event event, int requestedSeats);
    
    /**
     * Sends promotion notification emails to users who were promoted from waitlist.
     * 
     * @param promotedUsers List of users who were promoted
     */
    void sendPromotionNotificationEmails(List<PromotedUserDto> promotedUsers);
    
    /**
     * Sends individual promotion notification email to a promoted user.
     * 
     * @param promotedUser The user who was promoted
     */
    void sendPromotionNotificationEmail(PromotedUserDto promotedUser);
    
    /**
     * Sends booking cancellation confirmation email.
     * 
     * @param user The user who cancelled the booking
     * @param event The event that was cancelled
     * @param numberOfSeats Number of seats that were cancelled
     * @param bookingId The cancelled booking ID
     */
    void sendBookingCancellationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId);
    
    /**
     * Sends a general notification email to a user.
     * 
     * @param userEmail User's email address
     * @param subject Email subject
     * @param message Email message body
     */
    void sendNotificationEmail(String userEmail, String subject, String message);
}