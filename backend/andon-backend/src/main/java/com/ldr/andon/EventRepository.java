package com.ldr.andon;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
        select e from Event e
        where (:station is null or e.station = :station)
          and e.startedAt <= :to
          and (e.endedAt is null or e.endedAt >= :from)
        order by e.startedAt asc
    """)
    Page<Event> findOverlapping(
            @Param("station") String station,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            Pageable pageable
    );

    Optional<Event> findFirstByStationAndEventTypeInAndEndedAtIsNullOrderByStartedAtDesc(
            String station, List<EventType> types
    );

    Optional<Event> findFirstByStationAndEventTypeInOrderByStartedAtDesc(
            String station, List<EventType> types
    );
}
