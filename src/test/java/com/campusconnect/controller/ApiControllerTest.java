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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthController.class, EventController.class, RegistrationController.class,
        StudentController.class, AdminController.class, LoginController.class
})
@Import({SecurityConfig.class, ApiMapper.class, GlobalExceptionHandler.class})
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private EventService eventService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private com.campusconnect.repository.UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void publicEventListingReturnsOk() throws Exception {
        Event event = validEvent();
        when(eventService.getAllEvents(any())).thenReturn(new PageImpl<>(List.of(event)));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Java Workshop"));
    }

    @Test
    void authenticationEndpointsRegisterLoginAndLogout() throws Exception {
        User user = new User("Student", "student@example.com", "hashed", Role.STUDENT);
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(userService.getUserByEmail("student@example.com")).thenReturn(user);
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("student@example.com", "password"));

        mockMvc.perform(post("/api/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Student","email":"student@example.com","password":"password123"}
                                """))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"student@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
        mockMvc.perform(post("/api/auth/logout").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void publicEventFiltersAndStatusListsReturnOk() throws Exception {
        when(eventService.searchEvents(org.mockito.ArgumentMatchers.eq("java"),
                org.mockito.ArgumentMatchers.eq(""), any()))
                .thenReturn(new PageImpl<>(List.of(validEvent())));
        when(eventService.getUpcomingEvents()).thenReturn(List.of(validEvent()));
        when(eventService.getCompletedEvents()).thenReturn(List.of());

        mockMvc.perform(get("/api/events").param("title", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Java Workshop"));
        mockMvc.perform(get("/api/events/upcoming"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/events/completed"))
                .andExpect(status().isOk());
    }

    @Test
    void invalidEventRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/admin/events")
                        .with(csrf())
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","category":"","eventDate":"2020-01-01",
                                 "startTime":"12:00:00","endTime":"10:00:00",
                                 "venue":"","capacity":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCanCreateEvent() throws Exception {
        when(eventService.createEvent(any(Event.class))).thenReturn(validEvent());

        mockMvc.perform(post("/api/admin/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEventJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Java Workshop"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCanUpdateAndDeleteEvent() throws Exception {
        when(eventService.updateEvent(any(), any(Event.class))).thenReturn(validEvent());

        mockMvc.perform(put("/api/admin/events/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(validEventJson()))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/admin/events/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void unauthenticatedAdminRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void studentCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginPageIsAvailableForFormAuthentication() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void stateChangingRequestRequiresCsrfToken() throws Exception {
        mockMvc.perform(post("/api/admin/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEventJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void studentCanViewDashboardAndRegistrations() throws Exception {
        User student = new User("Student", "student@example.com", "hashed", Role.STUDENT);
        when(userService.getUserByEmail("student@example.com")).thenReturn(student);
        when(eventService.getUpcomingEvents()).thenReturn(List.of());
        when(registrationService.getStudentRegistrations(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/student/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upcomingEvents").isArray());
        mockMvc.perform(get("/api/student/registrations"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void studentCanRegisterAndCancel() throws Exception {
        User student = new User("Student", "student@example.com", "hashed", Role.STUDENT);
        when(userService.getUserByEmail("student@example.com")).thenReturn(student);
        when(registrationService.registerStudent(null, 1L)).thenReturn(
                new com.campusconnect.entity.Registration(student, validEvent(),
                        com.campusconnect.enums.RegistrationStatus.REGISTERED));
        when(registrationService.cancelRegistration(null, 1L)).thenReturn(
                new com.campusconnect.entity.Registration(student, validEvent(),
                        com.campusconnect.enums.RegistrationStatus.CANCELLED));

        mockMvc.perform(post("/api/registrations").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"eventId\":1}"))
                .andExpect(status().isCreated());
        mockMvc.perform(delete("/api/registrations/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminDashboardReturnsStatistics() throws Exception {
        when(userService.countStudents()).thenReturn(5L);
        when(eventService.countEvents()).thenReturn(3L);
        when(eventService.getUpcomingEvents()).thenReturn(List.of());
        when(eventService.getCompletedEvents()).thenReturn(List.of());
        when(registrationService.countRegistrations()).thenReturn(12L);

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").value(5));
    }

    private Event validEvent() {
        return new Event("Java Workshop", "Spring Boot", "Workshop",
                LocalDate.now().plusDays(10), LocalTime.of(10, 0),
                LocalTime.of(12, 0), "Lab 1", 50);
    }

    private String validEventJson() {
        return """
                {"title":"Java Workshop","description":"Spring Boot",
                 "category":"Workshop","eventDate":"2030-01-15",
                 "startTime":"10:00:00","endTime":"12:00:00",
                 "venue":"Lab 1","capacity":50}
                """;
    }
}
