package com.campusconnect.dto;

import com.campusconnect.enums.RegistrationStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class RegistrationDtos {

    private RegistrationDtos() {
    }

    public record RegistrationResponse(
            Long id, Long userId, Long eventId, LocalDateTime registeredAt,
            RegistrationStatus status) {
    }

    public record RegistrationRequest(@NotNull Long eventId) {
    }
}
