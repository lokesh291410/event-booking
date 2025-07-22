package krashi.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import krashi.server.entity.EventFeedback;

@Repository
public interface EventFeedbackRepository extends JpaRepository<EventFeedback, Long> {
    List<EventFeedback> findByEventId(Long eventId);
    List<EventFeedback> findByUserId(Long userId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    
    @Query("SELECT AVG(ef.rating) FROM EventFeedback ef WHERE ef.event.id = :eventId")
    Double getAverageRatingByEventId(Long eventId);
}
