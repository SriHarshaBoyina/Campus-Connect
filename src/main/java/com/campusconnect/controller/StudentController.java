package com.campusconnect.controller;

import com.campusconnect.dto.DashboardDtos;
import com.campusconnect.dto.EventDtos;
import com.campusconnect.dto.RegistrationDtos;
import com.campusconnect.mapper.ApiMapper;
import com.campusconnect.service.EventService;
import com.campusconnect.service.RegistrationService;
import com.campusconnect.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final UserService userService;
    private final EventService eventService;
    private final RegistrationService registrationService;
    private final ApiMapper mapper;

    public StudentController(UserService userService, EventService eventService,
                             RegistrationService registrationService, ApiMapper mapper) {
        this.userService = userService;
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.mapper = mapper;
    }

    @GetMapping("/registrations")
    public java.util.List<RegistrationDtos.RegistrationResponse> registrations(Authentication authentication) {
        Long userId = userService.getUserByEmail(authentication.getName()).getId();
        return registrationService.getStudentRegistrations(userId).stream()
                .map(mapper::toRegistrationResponse).toList();
    }

    @GetMapping("/dashboard")
    public DashboardDtos.StudentDashboard dashboard(Authentication authentication) {
        Long userId = userService.getUserByEmail(authentication.getName()).getId();
        return new DashboardDtos.StudentDashboard(
                eventService.getUpcomingEvents().stream().map(mapper::toEventResponse).toList(),
                registrationService.getStudentRegistrations(userId).stream()
                        .map(mapper::toRegistrationResponse).toList());
    }
}
