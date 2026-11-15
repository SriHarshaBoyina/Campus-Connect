package com.campusconnect.service;

import com.campusconnect.entity.Event;
import com.campusconnect.entity.Registration;
import com.campusconnect.entity.User;
import com.campusconnect.enums.RegistrationStatus;
import com.campusconnect.enums.Role;
import com.campusconnect.exception.BusinessRuleException;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.repository.RegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    private RegistrationService registrationService;
    private User student;
    private Event event;

    @BeforeEach
    void setUp() throws Exception {
        registrationService = new RegistrationService(
                userService, eventRepository, registrationRepository);
        student = new User("Student", "student@example.com", "hashed", Role.STUDENT);
        event = new Event(
                "Campus event", "Description", "Workshop",
                LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0), "Room 1", 2
        );
        setId(student, 1L);
        setId(event, 10L);
    }

    @Test
    void registerStudentCreatesRegistrationWhenCapacityExists() {
        when(userService.getUserById(1L)).thenReturn(student);
        when(eventRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(event));
        when(registrationRepository.findByUserIdAndEventId(1L, 10L)).thenReturn(Optional.empty());
        when(registrationRepository.countByEventIdAndStatus(10L, RegistrationStatus.REGISTERED))
                .thenReturn(1L);
        when(registrationRepository.save(org.mockito.ArgumentMatchers.any(Registration.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Registration result = registrationService.registerStudent(1L, 10L);

        assertEquals(RegistrationStatus.REGISTERED, result.getStatus());
        verify(registrationRepository).save(result);
    }

    @Test
    void registerStudentRejectsDuplicateRegistration() {
        Registration existing = new Registration(student, event, RegistrationStatus.REGISTERED);
        when(userService.getUserById(1L)).thenReturn(student);
        when(eventRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(event));
        when(registrationRepository.findByUserIdAndEventId(1L, 10L))
                .thenReturn(Optional.of(existing));

        assertThrows(BusinessRuleException.class,
                () -> registrationService.registerStudent(1L, 10L));
    }

    @Test
    void registerStudentRejectsFullEvent() {
        when(userService.getUserById(1L)).thenReturn(student);
        when(eventRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(event));
        when(registrationRepository.findByUserIdAndEventId(1L, 10L)).thenReturn(Optional.empty());
        when(registrationRepository.countByEventIdAndStatus(10L, RegistrationStatus.REGISTERED))
                .thenReturn(2L);

        assertThrows(BusinessRuleException.class,
                () -> registrationService.registerStudent(1L, 10L));
    }

    @Test
    void cancelRegistrationChangesStatus() {
        Registration registration = new Registration(student, event, RegistrationStatus.REGISTERED);
        when(registrationRepository.findByUserIdAndEventId(1L, 10L))
                .thenReturn(Optional.of(registration));

        registrationService.cancelRegistration(1L, 10L);

        assertEquals(RegistrationStatus.CANCELLED, registration.getStatus());
        verify(registrationRepository).save(registration);
    }

    private static void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }
}
