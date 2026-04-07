package com.example.moneymanager.controller;

import com.example.moneymanager.dto.AuthDto;
import com.example.moneymanager.dto.ProfileDto;
import com.example.moneymanager.exceptions.ResourceNotFoundException;
import com.example.moneymanager.services.JwtService;
import com.example.moneymanager.services.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/profile")

public class ProfileController {

    private final ProfileService profileService;
    private final JwtService jwtService;

    @Value("${money.manager.frontend.url}")
    String frontendURL;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.registerProfile(profileDto));
    }

    @CrossOrigin("${money.manager.frontend.url}")
    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String activationToken) {
        boolean isActivated = profileService.activateProfile(activationToken);
        if (isActivated) return ResponseEntity.ok("Profile is Activated Successfully");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token not found or is activated already");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDto authDto) {
        if (!profileService.existsByEmail(authDto.getEmail())) {
            throw new ResourceNotFoundException("Account with email " + authDto.getEmail() + " not found");
        }
        if (!profileService.isAccountActive(authDto.getEmail())) {
            throw new AccessDeniedException("Account is not Active. Please activate it first");
        }
        Map<String, Object> response = profileService.authenticateAndGenerateToken(authDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh/{refreshToken}")
    public ResponseEntity<Map<String, Object>> refreshToken(@PathVariable String refreshToken) {
        Map<String, Object> response = profileService.generateRefreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<ProfileDto> getPublicProfile() {
        return ResponseEntity.ok(profileService.getPublicProfile(null));
    }
}