package com.example.moneymanager.services;

import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.exceptions.ResourceNotFoundException;
import com.example.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {
    private final ProfileRepository profileRepository;

    @Override
    // 1. We use @Cacheable to store the UserDetails object in Redis.
    // 2. 'value = "user"' defines the Redis prefix (e.g., "user::...").
    // 3. 'key = "#email"' ensures we look up the user by their unique identifier.
    @Cacheable(value = "user", key = "#email")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        /* COMMENT:
           Spring Security calls this method constantly to check credentials and roles.
           By caching this, we avoid a "SELECT * FROM tbl_profiles" on every API call.
           NOTE: The returned 'User' (UserDetails) must implement Serializable.
        */

        ProfileEntity existingUser = (ProfileEntity) profileRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with email: " + email));

        return User.builder()
                .username(email)
                .password(existingUser.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}