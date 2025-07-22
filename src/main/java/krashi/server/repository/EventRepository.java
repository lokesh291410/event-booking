package krashi.server.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import krashi.server.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status);
    List<Event> findByCategory(String category);
    List<Event> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Event> findByTitleContainingIgnoreCase(String title);
    
    // Methods for admin-specific event management
    List<Event> findByCreatedBy_Id(Long createdById);
    List<Event> findByCreatedBy_IdAndStatus(Long createdById, String status);
    
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.dateTime > :now ORDER BY e.dateTime ASC")
    List<Event> findUpcomingPublicEvents(LocalDateTime now);
    
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.availableSeats > 0 ORDER BY e.dateTime ASC")
    List<Event> findAvailableEvents();
}
