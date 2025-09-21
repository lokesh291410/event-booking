package krashi.server.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import krashi.server.dto.BookingDto;
import krashi.server.dto.EventDetailResponseDto;
import krashi.server.dto.EventDto;
import krashi.server.dto.EventResponseDto;
import krashi.server.entity.Booking;
import krashi.server.entity.Event;
import krashi.server.entity.EventFeedback;
import krashi.server.entity.UserInfo;
import krashi.server.entity.Waitlist;
import krashi.server.exception.AccessDeniedException;
import krashi.server.exception.BadRequestException;
import krashi.server.exception.ResourceNotFoundException;
import krashi.server.mapping.BookingToDto;
import krashi.server.mapping.EventToDto;
import krashi.server.repository.BookingRepository;
import krashi.server.repository.EventFeedbackRepository;
import krashi.server.repository.EventRepository;
import krashi.server.repository.WaitlistRepository;
import krashi.server.service.AdminService;
import krashi.server.service.AuthenticationService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final WaitlistRepository waitlistRepository;
    private final EventFeedbackRepository eventFeedbackRepository;
    private final AuthenticationService authenticationService;

    private void verifyEventOwnership(Event event, UserInfo admin) {
        if (event.getCreatedBy() == null) {
            throw new BadRequestException("Event has no creator assigned");
        }
        
        if (admin == null || admin.getId() == null) {
            throw new BadRequestException("Invalid admin user");
        }
        
        if (!event.getCreatedBy().getId().equals(admin.getId())) {
            throw new AccessDeniedException("You can only access events you created");
        }
    }

    @Override
    public ResponseEntity<?> createEvent(EventDto eventDto) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        
        LocalDate eventDate = LocalDate.parse(eventDto.getDate());
        LocalTime eventTime = LocalTime.parse(eventDto.getTime());
        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);
        
        if (eventDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event date and time must be in the future");
        }
        
        event.setDateTime(eventDateTime);
        
        if (eventDto.getEndDate() != null && eventDto.getEndTime() != null) {
            LocalDate endDate = LocalDate.parse(eventDto.getEndDate());
            LocalTime endTime = LocalTime.parse(eventDto.getEndTime());
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            
            if (endDateTime.isBefore(eventDateTime)) {
                throw new BadRequestException("End date and time must be after start date and time");
            }
            
            event.setEndDateTime(endDateTime);
        }
        
        event.setLocation(eventDto.getLocation());
        event.setTotalSeats(eventDto.getTotalSeats());
        event.setAvailableSeats(eventDto.getTotalSeats());
        event.setCategory(eventDto.getCategory());
        event.setImageUrl(eventDto.getImageUrl());
        event.setPrice(eventDto.getPrice());
        event.setOrganizerName(eventDto.getOrganizerName());
        event.setOrganizerEmail(eventDto.getOrganizerEmail());
        event.setStatus("DRAFT");
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setCreatedBy(admin);

        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.ok("Event created successfully with ID: " + savedEvent.getId());
    }

    @Override
    public ResponseEntity<?> updateEvent(Long eventId, EventDto eventDto) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        
        LocalDate eventDate = LocalDate.parse(eventDto.getDate());
        LocalTime eventTime = LocalTime.parse(eventDto.getTime());
        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);
        
        if (!event.getStatus().equals("PUBLISHED") && eventDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event date and time must be in the future");
        }
        
        event.setDateTime(eventDateTime);
        
        if (eventDto.getEndDate() != null && eventDto.getEndTime() != null) {
            LocalDate endDate = LocalDate.parse(eventDto.getEndDate());
            LocalTime endTime = LocalTime.parse(eventDto.getEndTime());
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            
            if (endDateTime.isBefore(eventDateTime)) {
                throw new BadRequestException("End date and time must be after start date and time");
            }
            
            event.setEndDateTime(endDateTime);
        }
        
        event.setLocation(eventDto.getLocation());
        event.setCategory(eventDto.getCategory());
        event.setImageUrl(eventDto.getImageUrl());
        event.setPrice(eventDto.getPrice());
        event.setOrganizerName(eventDto.getOrganizerName());
        event.setOrganizerEmail(eventDto.getOrganizerEmail());
        event.setUpdatedAt(LocalDateTime.now());

        if (event.getStatus().equals("DRAFT")) {
            event.setTotalSeats(eventDto.getTotalSeats());
            event.setAvailableSeats(eventDto.getTotalSeats());
        }

        eventRepository.save(event);
        return ResponseEntity.ok("Event updated successfully");
    }

    @Override
    public ResponseEntity<?> deleteEvent(Long eventId) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        if (bookingRepository.existsByEvent_Id(eventId)) {
            throw new BadRequestException("Cannot delete event with existing bookings. Cancel the event instead.");
        }
        
        eventRepository.deleteById(eventId);
        return ResponseEntity.ok("Event deleted successfully");
    }

    @Override
    public ResponseEntity<?> publishEvent(Long eventId) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        if (!event.getStatus().equals("DRAFT")) {
            throw new BadRequestException("Only draft events can be published");
        }
        
        if (event.getDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot publish event with past date");
        }
        
        event.setStatus("PUBLISHED");
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
        
        return ResponseEntity.ok("Event published successfully");
    }

    @Override
    public ResponseEntity<?> cancelEvent(Long eventId, String reason) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        event.setStatus("CANCELLED");
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);
        
        return ResponseEntity.ok("Event cancelled successfully. Reason: " + reason);
    }

    @Override
    public ResponseEntity<?> getEventStatistics(Long eventId) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        long totalBookings = bookingRepository.countByEvent_Id(eventId);
        long waitlistCount = waitlistRepository.countByEvent_IdAndStatus(eventId, "WAITING");
        Double averageRating = eventFeedbackRepository.getAverageRatingByEventId(eventId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("event", event);
        stats.put("totalBookings", totalBookings);
        stats.put("bookedSeats", event.getTotalSeats() - event.getAvailableSeats());
        stats.put("availableSeats", event.getAvailableSeats());
        stats.put("waitlistCount", waitlistCount);
        stats.put("averageRating", averageRating != null ? averageRating : 0.0);
        
        return ResponseEntity.ok(stats);
    }

    @Override
    public ResponseEntity<?> getEventBookings(Long eventId) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        List<Booking> bookings = bookingRepository.findByEvent_Id(eventId);
        return ResponseEntity.ok(bookings);
    }

    @Override
    public ResponseEntity<?> getEventWaitlist(Long eventId) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        List<Waitlist> waitlist = waitlistRepository.findByEventIdAndStatus(eventId, "WAITING");
        return ResponseEntity.ok(waitlist);
    }

    @Override
    public ResponseEntity<?> getEventFeedback(Long eventId) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        List<EventFeedback> feedback = eventFeedbackRepository.findByEventId(eventId);
        return ResponseEntity.ok(feedback);
    }

    @Override
    public ResponseEntity<?> getAdminEvents() {
        Long adminId = authenticationService.getCurrentAdminId();
        
        List<Event> events = eventRepository.findByCreatedBy_Id(adminId);
        List<EventResponseDto> eventDtos = events.stream()
                .map(EventToDto::mapToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }

    @Override
    public ResponseEntity<?> getEventDetails(Long eventId) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        EventDetailResponseDto details = new EventDetailResponseDto();
        
        details.setId(event.getId());
        details.setTitle(event.getTitle());
        details.setDescription(event.getDescription());
        
        if (event.getDateTime() != null) {
            details.setDate(event.getDateTime().toLocalDate().format(dateFormatter));
            details.setTime(event.getDateTime().toLocalTime().format(timeFormatter));
        }
        
        if (event.getEndDateTime() != null) {
            details.setEndDate(event.getEndDateTime().toLocalDate().format(dateFormatter));
            details.setEndTime(event.getEndDateTime().toLocalTime().format(timeFormatter));
        }
        
        details.setLocation(event.getLocation());
        details.setTotalSeats(event.getTotalSeats());
        details.setAvailableSeats(event.getAvailableSeats());
        details.setCategory(event.getCategory());
        details.setImageUrl(event.getImageUrl());
        details.setPrice(event.getPrice());
        details.setOrganizerName(event.getOrganizerName());
        details.setOrganizerEmail(event.getOrganizerEmail());
        details.setStatus(event.getStatus());
        details.setCreatedAt(event.getCreatedAt());
        details.setUpdatedAt(event.getUpdatedAt());
        
        if (event.getCreatedBy() != null) {
            details.setCreatedById(event.getCreatedBy().getId());
            details.setCreatedByName(event.getCreatedBy().getName());
        }
        
        List<Booking> bookings = bookingRepository.findByEvent_Id(eventId);
        List<Waitlist> waitlist = waitlistRepository.findByEventIdAndStatus(eventId, "WAITING");
        List<EventFeedback> feedbacks = eventFeedbackRepository.findByEventId(eventId);
        Double averageRating = eventFeedbackRepository.getAverageRatingByEventId(eventId);
        
        long totalBookings = bookingRepository.countByEvent_Id(eventId);
        long totalFeedbacks = eventFeedbackRepository.countByEvent_Id(eventId);
        
        details.setTotalBookings((int)totalBookings);
        details.setBookedSeats(event.getTotalSeats() - event.getAvailableSeats());
        details.setWaitlistCount(waitlist.size());
        details.setAverageRating(averageRating != null ? averageRating : 0.0);
        details.setTotalFeedbacks((int)totalFeedbacks);
        
        List<BookingDto> recentBookings = bookings.stream()
                .sorted((b1, b2) -> b2.getBookingDateTime().compareTo(b1.getBookingDateTime()))
                .limit(5)
                .map(BookingToDto::mapToDto)
                .collect(Collectors.toList());
        details.setRecentBookings(recentBookings);
        
        List<EventDetailResponseDto.WaitlistSummaryDto> waitlistSummary = waitlist.stream()
                .map(w -> {
                    EventDetailResponseDto.WaitlistSummaryDto dto = new EventDetailResponseDto.WaitlistSummaryDto();
                    dto.setId(w.getId());
                    dto.setUserName(w.getUser().getUserName());
                    dto.setUserEmail(w.getUser().getEmail());
                    dto.setRequestedSeats(w.getRequestedSeats());
                    dto.setStatus(w.getStatus());
                    dto.setJoinedAt(w.getJoinedAt());
                    dto.setNotifiedAt(w.getNotifiedAt());
                    return dto;
                })
                .collect(Collectors.toList());
        details.setWaitlistUsers(waitlistSummary);
        
        List<EventDetailResponseDto.FeedbackSummaryDto> recentFeedbacks = feedbacks.stream()
                .sorted((f1, f2) -> f2.getSubmittedAt().compareTo(f1.getSubmittedAt()))
                .limit(5)
                .map(f -> {
                    EventDetailResponseDto.FeedbackSummaryDto dto = new EventDetailResponseDto.FeedbackSummaryDto();
                    dto.setId(f.getId());
                    dto.setUserName(f.getUser().getUserName());
                    dto.setRating(f.getRating());
                    dto.setComment(f.getComment());
                    dto.setWouldRecommend(f.isWouldRecommend());
                    dto.setSubmittedAt(f.getSubmittedAt());
                    return dto;
                })
                .collect(Collectors.toList());
        details.setRecentFeedbacks(recentFeedbacks);
        
        return ResponseEntity.ok(details);
    }

    @Override
    public ResponseEntity<?> notifyWaitlistUsers(Long eventId) {
        UserInfo admin = authenticationService.getCurrentAdmin();
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        verifyEventOwnership(event, admin);
        
        List<Waitlist> waitlist = waitlistRepository.findByEventIdAndStatus(eventId, "WAITING");
        
        for (Waitlist wait : waitlist) {
            wait.setStatus("NOTIFIED");
            wait.setNotifiedAt(LocalDateTime.now());
            waitlistRepository.save(wait);
        }
        
        return ResponseEntity.ok("Notified " + waitlist.size() + " users on waitlist");
    }

}
