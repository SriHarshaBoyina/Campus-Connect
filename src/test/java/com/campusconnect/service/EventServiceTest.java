package com.campusconnect.service;

import com.campusconnect.entity.Event;
import com.campusconnect.exception.BusinessRuleException;
import com.campusconnect.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void createEventRejectsPastDate() {
        Event event = new Event(
                "Past event", "Description", "Seminar",
                LocalDate.now().minusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0), "Room 1", 10
        );

        assertThrows(BusinessRuleException.class, () -> eventService.createEvent(event));
    }

    @Test
    void createEventRejectsInvalidTimeRange() {
        Event event = new Event(
                "Invalid event", "Description", "Seminar",
                LocalDate.now().plusDays(1), LocalTime.of(11, 0),
                LocalTime.of(10, 0), "Room 1", 10
        );

        assertThrows(BusinessRuleException.class, () -> eventService.createEvent(event));
    }

    @Test
    void createEventSavesValidEvent() {
        Event event = new Event(
                "Valid event", "Description", "Seminar",
                LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0), "Room 1", 10
        );

        eventService.createEvent(event);

        verify(eventRepository).save(event);
    }
}
