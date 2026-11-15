package com.campusconnect.repository;

import com.campusconnect.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :eventId")
    Optional<Event> findByIdForUpdate(@Param("eventId") Long eventId);

    List<Event> findByCategory(String category);

    List<Event> findByTitleContainingIgnoreCase(String title);

    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate date);

    List<Event> findByEventDateBeforeOrderByEventDateDesc(LocalDate date);

    List<Event> findByEventDateLessThanEqualOrderByEventDateDesc(LocalDate date);

    Page<Event> findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCase(
            String title, String category, Pageable pageable);
}
