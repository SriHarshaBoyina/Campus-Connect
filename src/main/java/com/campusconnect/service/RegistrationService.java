package com.campusconnect.service;

import com.campusconnect.entity.Event;
import com.campusconnect.entity.Registration;
import com.campusconnect.entity.User;
import com.campusconnect.enums.RegistrationStatus;
import com.campusconnect.enums.Role;
import com.campusconnect.exception.BusinessRuleException;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistrationService {

    private final UserService userService;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public RegistrationService(UserService userService, EventRepository eventRepository,
                               RegistrationRepository registrationRepository) {
        this.userService = userService;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional
    public Registration registerStudent(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        if (user.getRole() != Role.STUDENT) {
            throw new BusinessRuleException("Only students can register for events");
        }

        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));
        if (event.getEventDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Students cannot register for completed events");
        }

        Registration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElse(null);
        if (registration != null && registration.getStatus() == RegistrationStatus.REGISTERED) {
            throw new BusinessRuleException("Student is already registered for this event");
        }

        long activeRegistrations = registrationRepository.countByEventIdAndStatus(
                eventId, RegistrationStatus.REGISTERED);
        if (activeRegistrations >= event.getCapacity()) {
            throw new BusinessRuleException("Event capacity is full");
        }

        if (registration == null) {
            registration = new Registration(user, event, RegistrationStatus.REGISTERED);
        } else {
            registration.reactivate();
        }
        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration cancelRegistration(Long userId, Long eventId) {
        Registration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new BusinessRuleException("Registration is already cancelled");
        }
        registration.cancel();
        return registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public List<Registration> getStudentRegistrations(Long userId) {
        return registrationRepository.findWithEventByUserIdOrderByRegisteredAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long countRegistrations() {
        return registrationRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Registration> getActiveRegistrationsForEvent(Long eventId) {
        return registrationRepository.findWithUserByEventIdAndStatus(
                eventId, RegistrationStatus.REGISTERED);
    }
}
