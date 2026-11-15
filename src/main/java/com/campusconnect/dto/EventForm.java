package com.campusconnect.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventForm {

    @NotBlank
    @Size(max = 150)
    private String title;

    @Size(max = 5000)
    private String description;

    @NotBlank
    @Size(max = 50)
    private String category;

    @NotNull
    @FutureOrPresent
    private LocalDate eventDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotBlank
    @Size(max = 150)
    private String venue;

    @NotNull
    @Min(1)
    private Integer capacity;

    public EventForm() {
    }

    public EventForm(EventDtos.EventResponse event) {
        title = event.title();
        description = event.description();
        category = event.category();
        eventDate = event.eventDate();
        startTime = event.startTime();
        endTime = event.endTime();
        venue = event.venue();
        capacity = event.capacity();
    }

    public EventDtos.EventRequest toRequest() {
        return new EventDtos.EventRequest(title, description, category, eventDate,
                startTime, endTime, venue, capacity);
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
