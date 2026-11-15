package com.campusconnect.service;

import com.campusconnect.entity.Event;
import com.campusconnect.exception.BusinessRuleException;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Event createEvent(Event event) {
        validateEvent(event);
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));
    }

    @Transactional(readOnly = true)
    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Transactional
    public Event updateEvent(Long eventId, Event updatedEvent) {
        Event existingEvent = getEventById(eventId);
        validateEvent(updatedEvent);
        existingEvent.updateDetails(
                updatedEvent.getTitle(),
                updatedEvent.getDescription(),
                updatedEvent.getCategory(),
                updatedEvent.getEventDate(),
                updatedEvent.getStartTime(),
                updatedEvent.getEndTime(),
                updatedEvent.getVenue(),
                updatedEvent.getCapacity()
        );
        return eventRepository.save(existingEvent);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = getEventById(eventId);
        eventRepository.delete(event);
    }

    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        java.time.LocalTime now = java.time.LocalTime.now();
        return eventRepository.findByEventDateGreaterThanEqualOrderByEventDateAsc(today).stream()
                .filter(event -> event.getEventDate().isAfter(today)
                        || event.getEndTime().isAfter(now))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Event> getCompletedEvents() {
        LocalDate today = LocalDate.now();
        java.time.LocalTime now = java.time.LocalTime.now();
        return eventRepository.findByEventDateLessThanEqualOrderByEventDateDesc(today).stream()
                .filter(event -> event.getEventDate().isBefore(today)
                        || !event.getEndTime().isAfter(now))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Event> searchEvents(String title, String category) {
        if (title != null && !title.isBlank()) {
            return eventRepository.findByTitleContainingIgnoreCase(title.trim());
        }
        if (category != null && !category.isBlank()) {
            return eventRepository.findByCategory(category.trim());
        }
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long countEvents() {
        return eventRepository.count();
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Event> searchEvents(String title, String category,
                                                                     org.springframework.data.domain.Pageable pageable) {
        return eventRepository.findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCase(
                title == null ? "" : title.trim(),
                category == null ? "" : category.trim(), pageable);
    }

    private void validateEvent(Event event) {
        if (event.getCapacity() == null || event.getCapacity() <= 0) {
            throw new BusinessRuleException("Event capacity must be greater than zero");
        }
        if (event.getEventDate() == null || event.getEventDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Event date cannot be in the past");
        }
        if (event.getStartTime() == null || event.getEndTime() == null
                || !event.getEndTime().isAfter(event.getStartTime())) {
            throw new BusinessRuleException("Event end time must be later than start time");
        }
    }
}
