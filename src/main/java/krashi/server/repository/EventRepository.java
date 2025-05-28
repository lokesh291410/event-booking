package krashi.server.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import krashi.server.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
}
