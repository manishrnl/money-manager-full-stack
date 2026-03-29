package com.example.moneymanager.controller;

import com.example.moneymanager.dto.AuthDto;
import com.example.moneymanager.dto.ProfileDto;
import com.example.moneymanager.services.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/profile")

public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.registerProfile(profileDto));
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String activationToken) {
        boolean isActivated = profileService.activateProfile(activationToken);
        if (isActivated) return ResponseEntity.ok("Profile is Activated Successfully");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token not found or is activated already");
    }

    @PostMapping("/login")

    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDto authDto) {
        try {
            if (!profileService.isAccountActive(authDto.getEmail()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message",
                        "Account is not Active . Please Activate it first"));
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDto);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",
                    ex.getMessage()));
        }
    }

}