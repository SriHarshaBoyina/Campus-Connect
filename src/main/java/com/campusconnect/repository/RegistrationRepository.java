package com.campusconnect.repository;

import com.campusconnect.entity.Registration;
import com.campusconnect.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);

    List<Registration> findByUserIdOrderByRegisteredAtDesc(Long userId);

    @Query("select r from Registration r join fetch r.event where r.user.id = :userId order by r.registeredAt desc")
    List<Registration> findWithEventByUserIdOrderByRegisteredAtDesc(@Param("userId") Long userId);

    List<Registration> findByEventIdAndStatus(Long eventId, RegistrationStatus status);

    long countByEventIdAndStatus(Long eventId, RegistrationStatus status);

    long countByStatus(RegistrationStatus status);

    @Query("select r from Registration r join fetch r.user where r.event.id = :eventId and r.status = :status")
    List<Registration> findWithUserByEventIdAndStatus(@Param("eventId") Long eventId,
                                                       @Param("status") RegistrationStatus status);
}
