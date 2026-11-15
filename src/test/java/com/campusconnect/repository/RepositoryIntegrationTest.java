package com.campusconnect.repository;

import com.campusconnect.entity.Event;
import com.campusconnect.entity.Registration;
import com.campusconnect.entity.User;
import com.campusconnect.enums.RegistrationStatus;
import com.campusconnect.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Test
    void persistsRelationshipsAndCountsActiveRegistrations() {
        User user = userRepository.saveAndFlush(
                new User("Student", "student@example.com", "hashed", Role.STUDENT));
        Event event = eventRepository.saveAndFlush(new Event(
                "Workshop", "Description", "Workshop", LocalDate.now().plusDays(2),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "Lab 1", 10));

        registrationRepository.saveAndFlush(
                new Registration(user, event, RegistrationStatus.REGISTERED));

        assertEquals(1, registrationRepository.countByEventIdAndStatus(
                event.getId(), RegistrationStatus.REGISTERED));
        assertEquals(1, registrationRepository.findWithUserByEventIdAndStatus(
                event.getId(), RegistrationStatus.REGISTERED).size());
    }

    @Test
    void databaseRejectsDuplicateUserEmail() {
        userRepository.saveAndFlush(new User("One", "same@example.com", "hashed", Role.STUDENT));

        assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.saveAndFlush(
                        new User("Two", "same@example.com", "hashed", Role.STUDENT)));
    }
}
