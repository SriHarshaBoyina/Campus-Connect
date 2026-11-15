package com.campusconnect.mapper;

import com.campusconnect.dto.AuthDtos;
import com.campusconnect.dto.EventDtos;
import com.campusconnect.dto.RegistrationDtos;
import com.campusconnect.entity.Event;
import com.campusconnect.entity.Registration;
import com.campusconnect.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ApiMapper {

    public AuthDtos.UserResponse toUserResponse(User user) {
        return new AuthDtos.UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public EventDtos.EventResponse toEventResponse(Event event) {
        return new EventDtos.EventResponse(
                event.getId(), event.getTitle(), event.getDescription(), event.getCategory(),
                event.getEventDate(), event.getStartTime(), event.getEndTime(),
                event.getVenue(), event.getCapacity(), event.getCreatedAt());
    }

    public RegistrationDtos.RegistrationResponse toRegistrationResponse(Registration registration) {
        return new RegistrationDtos.RegistrationResponse(
                registration.getId(), registration.getUser().getId(), registration.getEvent().getId(),
                registration.getRegisteredAt(), registration.getStatus());
    }

    public Event toEvent(EventDtos.EventRequest request) {
        return new Event(request.title(), request.description(), request.category(),
                request.eventDate(), request.startTime(), request.endTime(),
                request.venue(), request.capacity());
    }
}
