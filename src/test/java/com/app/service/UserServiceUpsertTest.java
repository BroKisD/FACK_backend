package com.app.service;

import com.app.dto.UserDTO;
import com.app.entity.User;
import com.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("User Service - Upsert Tests")
class UserServiceUpsertTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Should return existing user if email exists")
    void testUpsertUserWhenEmailExists() {
        String email = "existing@example.com";
        String name = "Existing User";
        
        User existingUser = new User();
        existingUser.setId("123e4567-e89b-12d3-a456-426614174000");
        existingUser.setName("Existing User");
        existingUser.setEmail(email);
        existingUser.setRole("student");
        existingUser.setStatus("active");
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        
        UserDTO result = userService.upsertUser(name, email);
        
        assertNotNull(result);
        assertEquals("123e4567-e89b-12d3-a456-426614174000", result.getId());
        assertEquals(email, result.getEmail());
        
        // Should not call save
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create new user if email does not exist")
    void testUpsertUserWhenEmailDoesNotExist() {
        String email = "newstudent@example.com";
        String name = "New Student";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        User newUser = new User();
        newUser.setId("new-id-uuid");
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setRole("student");
        newUser.setStatus("active");
        
        when(userRepository.save(any())).thenReturn(newUser);
        
        UserDTO result = userService.upsertUser(name, email);
        
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals("student", result.getRole());
        assertEquals("active", result.getStatus());
        
        // Should call save once
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should set correct role and status for new student")
    void testUpsertUserSetsCorrectDefaults() {
        String email = "student@example.com";
        String name = "Test Student";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        User savedUser = new User();
        savedUser.setId("new-uuid");
        savedUser.setName(name);
        savedUser.setEmail(email);
        savedUser.setRole("student");
        savedUser.setStatus("active");
        
        when(userRepository.save(any())).thenReturn(savedUser);
        
        UserDTO result = userService.upsertUser(name, email);
        
        assertEquals("student", result.getRole());
        assertEquals("active", result.getStatus());
    }

    @Test
    @DisplayName("Should handle multiple upserts with same email")
    void testMultipleUpsertsWithSameEmail() {
        String email = "same@example.com";
        
        User existingUser = new User();
        existingUser.setId("existing-id");
        existingUser.setEmail(email);
        existingUser.setRole("student");
        existingUser.setStatus("active");
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        
        // First upsert
        UserDTO result1 = userService.upsertUser("User One", email);
        // Second upsert
        UserDTO result2 = userService.upsertUser("User Two", email);
        
        assertEquals(result1.getId(), result2.getId());
        
        // Should not call save
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should differentiate between different emails")
    void testUpsertUserWithDifferentEmails() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        
        User user1 = new User();
        user1.setId("id-1");
        user1.setEmail(email1);
        user1.setRole("student");
        
        User user2 = new User();
        user2.setId("id-2");
        user2.setEmail(email2);
        user2.setRole("student");
        
        when(userRepository.findByEmail(email1)).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail(email2)).thenReturn(Optional.of(user2));
        
        UserDTO result1 = userService.upsertUser("User 1", email1);
        UserDTO result2 = userService.upsertUser("User 2", email2);
        
        assertNotEquals(result1.getId(), result2.getId());
    }
}
