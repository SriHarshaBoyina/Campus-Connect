package com.campusconnect.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class EventDtos {

    private EventDtos() {
    }

    public record EventRequest(
            @NotBlank @Size(max = 150) String title,
            @Size(max = 5000) String description,
            @NotBlank @Size(max = 50) String category,
            @NotNull @FutureOrPresent LocalDate eventDate,
            @NotNull LocalTime startTime,
            @NotNull LocalTime endTime,
            @NotBlank @Size(max = 150) String venue,
            @NotNull @Min(1) Integer capacity) {
    }

    public record EventResponse(
            Long id, String title, String description, String category,
            LocalDate eventDate, LocalTime startTime, LocalTime endTime,
            String venue, Integer capacity, LocalDateTime createdAt) {
    }
}
