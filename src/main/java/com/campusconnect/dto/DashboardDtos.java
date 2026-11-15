package com.campusconnect.dto;

import java.util.List;

public final class DashboardDtos {

    private DashboardDtos() {
    }

    public record StudentDashboard(
            List<EventDtos.EventResponse> upcomingEvents,
            List<RegistrationDtos.RegistrationResponse> registrations) {
    }

    public record AdminDashboard(
            long totalStudents, long totalEvents, long upcomingEvents,
            long completedEvents, long totalRegistrations) {
    }
}
