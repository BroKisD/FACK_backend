package com.app.service;

import com.app.dto.UserDTO;
import com.app.entity.User;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // CREATE
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getId() == null || userDTO.getId().isEmpty()) {
            userDTO.setId(UUID.randomUUID().toString());
        }
        User user = convertToEntity(userDTO);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // UPSERT - Update if exists by email, otherwise create
    public UserDTO upsertUser(String name, String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            // User exists, return their ID
            return convertToDTO(existingUser.get());
        } else {
            // Create new student user
            UserDTO newUser = new UserDTO();
            newUser.setId(UUID.randomUUID().toString());
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setRole("student");
            newUser.setStatus("active");
            return createUser(newUser);
        }
    }

    // READ - Get by ID
    public Optional<UserDTO> getUserById(String id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    // READ - Get all users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Get by email
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO);
    }

    // READ - Get by role
    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Get by status
    public List<UserDTO> getUsersByStatus(String status) {
        return userRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // UPDATE
    public Optional<UserDTO> updateUser(String id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    if (userDTO.getName() != null) user.setName(userDTO.getName());
                    if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
                    if (userDTO.getPasswordHash() != null) user.setPasswordHash(userDTO.getPasswordHash());
                    if (userDTO.getRole() != null) user.setRole(userDTO.getRole());
                    if (userDTO.getStatus() != null) user.setStatus(userDTO.getStatus());
                    if (userDTO.getLastLoginAt() != null) user.setLastLoginAt(userDTO.getLastLoginAt());
                    User updatedUser = userRepository.save(user);
                    return convertToDTO(updatedUser);
                });
    }

    // DELETE
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Helper methods
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(userDTO.getPasswordHash());
        user.setRole(userDTO.getRole());
        user.setStatus(userDTO.getStatus());
        user.setLastLoginAt(userDTO.getLastLoginAt());
        return user;
    }
}
