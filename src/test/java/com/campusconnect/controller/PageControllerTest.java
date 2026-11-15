package com.campusconnect.controller;

import com.campusconnect.config.SecurityConfig;
import com.campusconnect.entity.Event;
import com.campusconnect.entity.User;
import com.campusconnect.enums.Role;
import com.campusconnect.exception.GlobalExceptionHandler;
import com.campusconnect.mapper.ApiMapper;
import com.campusconnect.service.EventService;
import com.campusconnect.service.RegistrationService;
import com.campusconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = PageController.class)
@Import({SecurityConfig.class, ApiMapper.class, GlobalExceptionHandler.class})
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private com.campusconnect.repository.UserRepository userRepository;

    @Test
    void registrationPageRenders() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    void eventsPageRendersWithEvents() throws Exception {
        when(eventService.getAllEvents(any())).thenReturn(new PageImpl<>(List.of(validEvent())));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/list"));
    }

    @Test
    void eventDetailsPageRenders() throws Exception {
        when(eventService.getEventById(1L)).thenReturn(validEvent());

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/details"));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void studentDashboardAndRegistrationsRender() throws Exception {
        User user = new User("Student", "student@example.com", "hashed", Role.STUDENT);
        when(userService.getUserByEmail("student@example.com")).thenReturn(user);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(validEvent()));
        when(registrationService.getStudentRegistrations(null)).thenReturn(List.of());

        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/dashboard"));
        mockMvc.perform(get("/student/registrations"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/registrations"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminPagesRenderAndCreateWorkflowRedirects() throws Exception {
        when(eventService.searchEvents("", "")).thenReturn(List.of(validEvent()));
        when(eventService.getUpcomingEvents()).thenReturn(List.of());
        when(eventService.getCompletedEvents()).thenReturn(List.of());
        when(eventService.createEvent(any(Event.class))).thenReturn(validEvent());
        when(userService.countStudents()).thenReturn(1L);
        when(eventService.countEvents()).thenReturn(1L);
        when(registrationService.countRegistrations()).thenReturn(1L);

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"));
        mockMvc.perform(get("/admin/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/event-form"));
        mockMvc.perform(post("/admin/events").with(csrf())
                        .param("title", "New event")
                        .param("category", "Workshop")
                        .param("eventDate", "2030-01-15")
                        .param("startTime", "10:00")
                        .param("endTime", "12:00")
                        .param("venue", "Lab 1")
                        .param("capacity", "50"))
                .andExpect(status().is3xxRedirection());
    }

    private Event validEvent() {
        return new Event("Java Workshop", "Spring Boot", "Workshop",
                LocalDate.now().plusDays(10), LocalTime.of(10, 0),
                LocalTime.of(12, 0), "Lab 1", 50);
    }
}
