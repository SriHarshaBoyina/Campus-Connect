package com.campusconnect.controller;

import com.campusconnect.dto.DashboardDtos;
import com.campusconnect.dto.EventDtos;
import com.campusconnect.mapper.ApiMapper;
import com.campusconnect.service.EventService;
import com.campusconnect.service.RegistrationService;
import com.campusconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.campusconnect.dto.RegistrationDtos;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final EventService eventService;
    private final UserService userService;
    private final RegistrationService registrationService;
    private final ApiMapper mapper;

    public AdminController(EventService eventService, UserService userService,
                           RegistrationService registrationService, ApiMapper mapper) {
        this.eventService = eventService;
        this.userService = userService;
        this.registrationService = registrationService;
        this.mapper = mapper;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtos.EventResponse createEvent(@Valid @RequestBody EventDtos.EventRequest request) {
        return mapper.toEventResponse(eventService.createEvent(mapper.toEvent(request)));
    }

    @PutMapping("/events/{eventId}")
    public EventDtos.EventResponse updateEvent(@PathVariable Long eventId,
                                               @Valid @RequestBody EventDtos.EventRequest request) {
        return mapper.toEventResponse(eventService.updateEvent(eventId, mapper.toEvent(request)));
    }

    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @GetMapping("/dashboard")
    public DashboardDtos.AdminDashboard dashboard() {
        return new DashboardDtos.AdminDashboard(
                userService.countStudents(),
                eventService.countEvents(),
                eventService.getUpcomingEvents().size(),
                eventService.getCompletedEvents().size(),
                registrationService.countRegistrations());
    }

    @GetMapping("/events/{eventId}/registrations")
    public List<RegistrationDtos.RegistrationResponse> registeredStudents(@PathVariable Long eventId) {
        return registrationService.getActiveRegistrationsForEvent(eventId).stream()
                .map(mapper::toRegistrationResponse).toList();
    }
}
