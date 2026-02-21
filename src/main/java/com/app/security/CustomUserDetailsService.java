package com.app.security;

import com.app.entity.User;
import com.app.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return buildUserDetails(user);
    }

    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + normalizeRole(user.getRole()))
        );

        boolean enabled = user.getStatus() == null || !"disabled".equalsIgnoreCase(user.getStatus());
        boolean accountNonLocked = user.getStatus() == null || !"locked".equalsIgnoreCase(user.getStatus());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash() == null ? "" : user.getPasswordHash())
                .authorities(authorities)
                .accountLocked(!accountNonLocked)
                .disabled(!enabled)
                .build();
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        return role.trim().toUpperCase();
    }
}
