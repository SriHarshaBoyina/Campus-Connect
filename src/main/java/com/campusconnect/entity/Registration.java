package com.campusconnect.entity;

import com.campusconnect.enums.RegistrationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "registrations",
        indexes = {
                @Index(name = "idx_registrations_user_id", columnList = "user_id"),
                @Index(name = "idx_registrations_event_id", columnList = "event_id")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uk_registration_user_event",
                columnNames = {"user_id", "event_id"}
        )
)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @NotNull
    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegistrationStatus status;

    protected Registration() {
    }

    public Registration(User user, Event event, RegistrationStatus status) {
        this.user = user;
        this.event = event;
        this.status = status;
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Event getEvent() {
        return event;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void cancel() {
        this.status = RegistrationStatus.CANCELLED;
    }

    public void reactivate() {
        this.status = RegistrationStatus.REGISTERED;
    }
}
