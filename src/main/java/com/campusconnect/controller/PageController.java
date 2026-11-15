package com.campusconnect.controller;

import com.campusconnect.dto.EventForm;
import com.campusconnect.dto.RegisterForm;
import com.campusconnect.entity.Event;
import com.campusconnect.entity.User;
import com.campusconnect.enums.Role;
import com.campusconnect.exception.BusinessRuleException;
import com.campusconnect.mapper.ApiMapper;
import com.campusconnect.service.EventService;
import com.campusconnect.service.RegistrationService;
import com.campusconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PageController {

    private final EventService eventService;
    private final RegistrationService registrationService;
    private final UserService userService;
    private final ApiMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public PageController(EventService eventService, RegistrationService registrationService,
                          UserService userService, ApiMapper mapper, PasswordEncoder passwordEncoder) {
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.userService = userService;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/events";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm form, BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.createUser(new User(form.getName(), form.getEmail().trim().toLowerCase(),
                    passwordEncoder.encode(form.getPassword()), Role.STUDENT));
            redirectAttributes.addFlashAttribute("success", "Account created. Please log in.");
            return "redirect:/login";
        } catch (BusinessRuleException exception) {
            result.rejectValue("email", "duplicate", exception.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return user.getRole() == Role.ADMIN
                ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
    }

    @GetMapping("/events")
    public String events(@RequestParam(defaultValue = "") String title,
                         @RequestParam(defaultValue = "") String category,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "9") int size, Model model) {
        if (!title.isBlank() || !category.isBlank()) {
            model.addAttribute("events", eventService.searchEvents(title, category));
            model.addAttribute("paged", false);
        } else {
            Page<Event> eventPage = eventService.getAllEvents(
                    PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                            Sort.by("eventDate").ascending()));
            model.addAttribute("events", eventPage.getContent());
            model.addAttribute("eventPage", eventPage);
            model.addAttribute("paged", true);
        }
        model.addAttribute("title", title);
        model.addAttribute("category", category);
        return "events/list";
    }

    @GetMapping("/events/{eventId}")
    public String eventDetails(@PathVariable Long eventId, Model model) {
        model.addAttribute("event", mapper.toEventResponse(eventService.getEventById(eventId)));
        return "events/details";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents());
        model.addAttribute("registrations", registrationService.getStudentRegistrations(user.getId()));
        return "student/dashboard";
    }

    @GetMapping("/student/registrations")
    public String myRegistrations(Authentication authentication, Model model) {
        model.addAttribute("registrations",
                registrationService.getStudentRegistrations(currentUser(authentication).getId()));
        return "student/registrations";
    }

    @PostMapping("/student/registrations/{eventId}")
    public String registerForEvent(@PathVariable Long eventId, Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            registrationService.registerStudent(currentUser(authentication).getId(), eventId);
            redirectAttributes.addFlashAttribute("success", "You are registered for this event.");
        } catch (BusinessRuleException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/student/registrations/{eventId}/cancel")
    public String cancelRegistration(@PathVariable Long eventId, Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            registrationService.cancelRegistration(currentUser(authentication).getId(), eventId);
            redirectAttributes.addFlashAttribute("success", "Registration cancelled.");
        } catch (BusinessRuleException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/student/registrations";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalStudents", userService.countStudents());
        model.addAttribute("totalEvents", eventService.countEvents());
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents());
        model.addAttribute("completedEvents", eventService.getCompletedEvents());
        model.addAttribute("totalRegistrations", registrationService.countRegistrations());
        return "admin/dashboard";
    }

    @GetMapping("/admin/events")
    public String adminEvents(Model model) {
        model.addAttribute("events", eventService.searchEvents("", ""));
        return "admin/events";
    }

    @GetMapping("/admin/events/{eventId}/registrations")
    public String registeredStudents(@PathVariable Long eventId, Model model) {
        model.addAttribute("event", eventService.getEventById(eventId));
        model.addAttribute("registrations", registrationService.getActiveRegistrationsForEvent(eventId));
        return "admin/registrations";
    }

    @GetMapping("/admin/events/new")
    public String newEvent(Model model) {
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("formAction", "/admin/events");
        model.addAttribute("formTitle", "Create event");
        return "admin/event-form";
    }

    @PostMapping("/admin/events")
    public String createEvent(@Valid @ModelAttribute EventForm form, BindingResult result,
                              Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepareEventForm(model, form, "/admin/events", "Create event");
            return "admin/event-form";
        }
        try {
            eventService.createEvent(mapper.toEvent(form.toRequest()));
            redirectAttributes.addFlashAttribute("success", "Event created.");
            return "redirect:/admin/events";
        } catch (BusinessRuleException exception) {
            result.reject("event.invalid", exception.getMessage());
            prepareEventForm(model, form, "/admin/events", "Create event");
            return "admin/event-form";
        }
    }

    @GetMapping("/admin/events/{eventId}/edit")
    public String editEvent(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("eventForm", new EventForm(mapper.toEventResponse(event)));
        model.addAttribute("formAction", "/admin/events/" + eventId);
        model.addAttribute("formTitle", "Edit event");
        return "admin/event-form";
    }

    @PostMapping("/admin/events/{eventId}")
    public String updateEvent(@PathVariable Long eventId, @Valid @ModelAttribute EventForm form,
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepareEventForm(model, form, "/admin/events/" + eventId, "Edit event");
            return "admin/event-form";
        }
        try {
            eventService.updateEvent(eventId, mapper.toEvent(form.toRequest()));
            redirectAttributes.addFlashAttribute("success", "Event updated.");
            return "redirect:/admin/events";
        } catch (BusinessRuleException exception) {
            result.reject("event.invalid", exception.getMessage());
            prepareEventForm(model, form, "/admin/events/" + eventId, "Edit event");
            return "admin/event-form";
        }
    }

    @PostMapping("/admin/events/{eventId}/delete")
    public String deleteEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        eventService.deleteEvent(eventId);
        redirectAttributes.addFlashAttribute("success", "Event deleted.");
        return "redirect:/admin/events";
    }

    private User currentUser(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName());
    }

    private void prepareEventForm(Model model, EventForm form, String action, String title) {
        model.addAttribute("eventForm", form);
        model.addAttribute("formAction", action);
        model.addAttribute("formTitle", title);
    }
}
