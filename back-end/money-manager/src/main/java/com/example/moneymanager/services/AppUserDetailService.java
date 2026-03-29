package com.example.moneymanager.services;

import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.exceptions.ResourceNotFoundException;
import com.example.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity existingUser =
                (ProfileEntity) profileRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Profile not found with email : " + email));
        return User.builder()
                .username(email)
                .password(existingUser.getPassword())
                .authorities(Collections.emptyList())
                .build();

    }
}
