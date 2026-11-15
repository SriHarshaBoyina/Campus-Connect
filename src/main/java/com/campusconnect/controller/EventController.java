package com.campusconnect.controller;

import com.campusconnect.dto.EventDtos;
import com.campusconnect.mapper.ApiMapper;
import com.campusconnect.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final ApiMapper mapper;

    public EventController(EventService eventService, ApiMapper mapper) {
        this.eventService = eventService;
        this.mapper = mapper;
    }

    @GetMapping
    public Page<EventDtos.EventResponse> list(
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!title.isBlank() || !category.isBlank()) {
                PageRequest pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
                return eventService.searchEvents(title, category, pageable).map(mapper::toEventResponse);
        }
        PageRequest pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        return eventService.getAllEvents(pageable).map(mapper::toEventResponse);
    }

    @GetMapping("/{eventId}")
    public EventDtos.EventResponse get(@PathVariable Long eventId) {
        return mapper.toEventResponse(eventService.getEventById(eventId));
    }

    @GetMapping("/upcoming")
    public List<EventDtos.EventResponse> upcoming() {
        return eventService.getUpcomingEvents().stream().map(mapper::toEventResponse).toList();
    }

    @GetMapping("/completed")
    public List<EventDtos.EventResponse> completed() {
        return eventService.getCompletedEvents().stream().map(mapper::toEventResponse).toList();
    }
}
