package com.campusconnect.service;

import com.campusconnect.entity.User;
import com.campusconnect.exception.BusinessRuleException;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.campusconnect.enums.Role;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BusinessRuleException("Email is required");
        }
        user.normalizeEmail();
        String normalizedEmail = user.getEmail();
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new BusinessRuleException("A user with this email already exists");
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public long countStudents() {
        return userRepository.countByRole(Role.STUDENT);
    }
}
