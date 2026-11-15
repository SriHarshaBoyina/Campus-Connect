package com.campusconnect.service;

import com.campusconnect.entity.User;
import com.campusconnect.enums.Role;
import com.campusconnect.exception.BusinessRuleException;
import com.campusconnect.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserRejectsDuplicateEmail() {
        User user = new User("Student", "student@example.com", "hashed", Role.STUDENT);
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));

        assertThrows(BusinessRuleException.class, () -> userService.createUser(user));
    }

    @Test
    void createUserSavesNewUser() {
        User user = new User("Student", "Student@Example.com", "hashed", Role.STUDENT);
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.empty());

        userService.createUser(user);

        verify(userRepository).save(user);
    }
}
