package com.example.moneymanager.services;

import com.example.moneymanager.dto.AuthDto;
import com.example.moneymanager.dto.ProfileDto;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ModelMapper modelMapper;
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;

    @Value("&{app.activation.url}")
    private String activationURL;

    public ProfileDto registerProfile(ProfileDto profileDto) {
        profileDto.setPassword(passwordEncoder.encode(profileDto.getPassword()));
        profileDto.setActivationToken(UUID.randomUUID().toString());

        ProfileEntity newProfile = profileRepository.save(modelMapper.map(profileDto, ProfileEntity.class));
        newProfile = profileRepository.save(newProfile);
        //        Send Email Activation Link
        String activationLink = activationURL + "/api/v1/profile/activate?activationToken=" + newProfile.getActivationToken();
        String subject = "Activate your Money Manager Account To Access our dashboard and all features";
        String body = "Click on the following link to activate your account : " + activationLink;
        emailService.sendEmail(profileDto.getEmail(), subject, body);

        return modelMapper.map(newProfile, ProfileDto.class);
    }

    public Boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profileEntity -> {
                    profileEntity.setIsActive(true);
                    profileRepository.save(profileEntity);
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email) {
        Optional<ProfileEntity> users = profileRepository.findByEmail(email);
        if (users.get().getIsActive() == true)
            return true;
        else
            return false;
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ProfileEntity currentUser = profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Could not find users with email : " + email));
        return currentUser;
    }

    public ProfileDto getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null)
            currentUser = getCurrentProfile();
        else
            currentUser = profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Profile not found with email " + email));
        return modelMapper.map(currentUser, ProfileDto.class);
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDto.getEmail(),
                            authDto.getPassword())
            );

            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            ProfileEntity user = profileRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            sessionService.generateNewSession(user, refreshToken);

            return Map.of("id", user.getId(),
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            );
        } catch (Exception ex) {
            throw new RuntimeException("Invalid Email or password. Details Logs are : " + ex.getMessage());
        }

    }
}