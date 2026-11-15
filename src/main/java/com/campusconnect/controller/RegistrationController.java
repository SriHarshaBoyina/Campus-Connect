package com.campusconnect.controller;

import com.campusconnect.dto.RegistrationDtos;
import com.campusconnect.mapper.ApiMapper;
import com.campusconnect.service.RegistrationService;
import com.campusconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final ApiMapper mapper;

    public RegistrationController(RegistrationService registrationService, UserService userService,
                                   ApiMapper mapper) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public RegistrationDtos.RegistrationResponse register(
            @Valid @RequestBody RegistrationDtos.RegistrationRequest request,
            Authentication authentication) {
        Long userId = userService.getUserByEmail(authentication.getName()).getId();
        return mapper.toRegistrationResponse(
                registrationService.registerStudent(userId, request.eventId()));
    }

    @DeleteMapping("/{eventId}")
    public RegistrationDtos.RegistrationResponse cancel(
            @PathVariable Long eventId, Authentication authentication) {
        Long userId = userService.getUserByEmail(authentication.getName()).getId();
        return mapper.toRegistrationResponse(
                registrationService.cancelRegistration(userId, eventId));
    }
}
