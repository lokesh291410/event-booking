package krashi.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krashi.server.entity.Waitlist;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    List<Waitlist> findByEventIdAndStatus(Long eventId, String status);
    List<Waitlist> findByUserIdAndStatus(Long userId, String status);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}
