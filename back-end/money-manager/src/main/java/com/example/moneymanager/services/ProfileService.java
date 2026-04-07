package com.example.moneymanager.services;

import com.example.moneymanager.dto.AuthDto;
import com.example.moneymanager.dto.ProfileDto;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.entity.Session;
import com.example.moneymanager.exceptions.BadRequestException;
import com.example.moneymanager.exceptions.ResourceNotFoundException;
import com.example.moneymanager.repository.ProfileRepository;
import com.example.moneymanager.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor

public class ProfileService {

    private final ModelMapper modelMapper;
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;

    @Value("${money.manager.frontend.url}")
    private String frontendURL;

    public ProfileDto registerProfile(ProfileDto profileDto) {
        profileDto.setPassword(passwordEncoder.encode(profileDto.getPassword()));
        profileDto.setActivationToken(UUID.randomUUID().toString());

        ProfileEntity newProfile = profileRepository.save(modelMapper.map(profileDto, ProfileEntity.class));
        newProfile = profileRepository.save(newProfile);
        sendActivationEmail(profileDto.getEmail(), profileDto.getActivationToken());
        return modelMapper.map(newProfile, ProfileDto.class);
    }

    private void sendActivationEmail(String email, String activationToken) {
        String activationLink = frontendURL + "/activate?activationToken=" + activationToken;
        String subject = "🚀 Verify Your Money Manager Account";

        String body = "<html>" +
                "<body style='margin: 0; padding: 0; background-color: #f4f7fa; font-family: \"Segoe UI\", Helvetica, Arial, sans-serif;'>" +
                "<table width='100%' border='0' cellspacing='0' cellpadding='0'>" +
                "<tr><td align='center' style='padding: 40px 0;'>" +
                "<table width='600' border='0' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.05);'>" +
                "<tr><td style='background: linear-gradient(to right, #10b981, #059669); height: 8px;'></td></tr>" +
                "<tr><td style='padding: 50px 40px; text-align: center;'>" +
                "<div style='margin-bottom: 24px; font-size: 32px;'>🏦</div>" +
                "<h1 style='color: #1e293b; font-size: 28px; font-weight: 700; margin: 0 0 16px; letter-spacing: -0.5px;'>Almost there!</h1>" +
                "<p style='color: #475569; font-size: 16px; line-height: 26px; margin: 0 0 32px;'>Welcome to Money Manager. To protect your financial data and activate your dashboard, please confirm your email address below.</p>" +
                "<a href='" + activationLink + "' style='display: inline-block; background-color: #0f172a; color: #ffffff; padding: 18px 36px; font-weight: 600; text-decoration: none; border-radius: 12px; font-size: 16px; transition: all 0.3s ease;'>Verify My Email Address</a>" +
                "<p style='color: #94a3b8; font-size: 13px; margin-top: 40px; line-height: 20px;'>" +
                "Link not working? Copy this into your browser:<br>" +
                "<a href='#' style='color: #10b981; text-decoration: none;'>" + activationLink + "</a>" +
                "</p>" +
                "</td></tr>" +
                "<tr><td style='background-color: #f8fafc; padding: 30px; text-align: center; border-top: 1px solid #edf2f7;'>" +
                "<p style='color: #94a3b8; font-size: 12px; margin: 0;'>&copy; 2026 Money Manager &bull; Secure Financial Cloud</p>" +
                "</td></tr>" +
                "</table>" +
                "</td></tr>" +
                "</table>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(email, subject, body);
    }

    private void sendSuccessEmail(String email) {
        String loginLink = frontendURL + "/login";
        String subject = "🎉 You're All Set! Account Activated";

        String body = "<html>" +
                "<body style='margin: 0; padding: 0; background-color: #f4f7fa; font-family: \"Segoe UI\", Helvetica, Arial, sans-serif;'>" +
                "<table width='100%' border='0' cellspacing='0' cellpadding='0'>" +
                "<tr><td align='center' style='padding: 40px 0;'>" +
                "<table width='600' border='0' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border-radius: 20px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.05);'>" +
                "<tr><td style='padding: 50px 40px; text-align: center;'>" +
                "" +
                "<div style='display: inline-block; width: 80px; height: 80px; background-color: #ecfdf5; border-radius: 50%; margin-bottom: 24px;'>" +
                "<div style='color: #10b981; font-size: 40px; line-height: 80px;'>✓</div>" +
                "</div>" +
                "<h1 style='color: #1e293b; font-size: 28px; font-weight: 700; margin: 0 0 12px;'>Welcome to the Club!</h1>" +
                "<p style='color: #475569; font-size: 16px; line-height: 26px; margin: 0 0 32px;'>Your account is now fully verified. You're ready to start your journey toward financial freedom.</p>" +
                "<a href='" + loginLink + "' style='display: inline-block; background-color: #10b981; color: #ffffff; padding: 18px 40px; font-weight: 700; text-decoration: none; border-radius: 12px; font-size: 16px;'>Enter Dashboard</a>" +

                "" +
                "<div style='margin-top: 45px; padding: 30px; background-color: #f8fafc; border-radius: 16px; text-align: left;'>" +
                "<h4 style='color: #1e293b; margin: 0 0 15px; font-size: 14px; text-transform: uppercase; letter-spacing: 1px;'>3 Steps to get started:</h4>" +
                "<div style='margin-bottom: 12px; color: #64748b; font-size: 15px;'>🚀 <b>Create</b> your first expense category</div>" +
                "<div style='margin-bottom: 12px; color: #64748b; font-size: 15px;'>💰 <b>Set</b> a monthly budget limit</div>" +
                "<div style='color: #64748b; font-size: 15px;'>📈 <b>View</b> your wealth growth charts</div>" +
                "</div>" +
                "</td></tr>" +
                "<tr><td style='padding: 25px; text-align: center; border-top: 1px solid #f1f5f9;'>" +
                "<p style='color: #cbd5e1; font-size: 11px; margin: 0;'>Securely encrypted by Money Manager Protocol</p>" +
                "</td></tr>" +
                "</table>" +
                "</td></tr>" +
                "</table>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(email, subject, body);
    }

    public Boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profile.setActivationToken(null);   // Important: Clear token after use
                    profileRepository.save(profile);
                    sendSuccessEmail(profile.getEmail());
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    @Cacheable(cacheNames = "profiles", key = "#root.target.getCurrentUserEmail()")
    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ProfileEntity currentUser = profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Could not find users with email : " + email));
        return currentUser;
    }
    @Cacheable(cacheNames = "profiles", key = "#email ?: #root.target.getCurrentUserEmail()")
    public ProfileDto getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null)
            currentUser = getCurrentProfile();
        else
            currentUser = profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Profile not found with email " + email));
        return modelMapper.map(currentUser, ProfileDto.class);
    }

    public boolean existsByEmail(String email) {
        return profileRepository.existsByEmail(email);
    }


    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {
        try {

            boolean isActive = isAccountActive(authDto.getEmail());
            if (!isActive) {
                throw new BadRequestException("Account is not activated. Please check your email.");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDto.getEmail(),
                            authDto.getPassword())
            );

            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            ProfileEntity user = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // 1. Generate the Refresh Token string first
            String refreshTokenString = jwtService.generateRefreshToken(user);

            // 2. Create the Session record in DB (This handles the 2-session limit logic)
            // Ensure generateNewSession returns the saved Session object
            Session session = sessionService.generateNewSession(user, refreshTokenString);

            // 3. Generate Access Token using the Session ID from the DB
            // This links the JWT to a specific row in your Postgres table
            String accessToken = jwtService.generateAccessToken(user, session.getId());

            return Map.of(
                    "id", user.getId(),
                    "accessToken", accessToken,
                    "refreshToken", refreshTokenString,
                    "fullName", user.getFullName(),
                    "profileImageUrl", user.getProfileImageUrl() != null ? user.getProfileImageUrl() : ""
            );
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new BadRequestException("Invalid Email or password");
        } catch (Exception ex) {
            throw new RuntimeException("Authentication logic failed: " + ex.getMessage());
        }
    }

    public Map<String, Object> generateRefreshToken(String oldRefreshToken) {
        try {
            // 1. Validate the refresh token exists in DB and update activity
            sessionService.validateSession(oldRefreshToken);

            // 2. Extract User ID from the refresh token
            Long userId = jwtService.getUserIdFromRefreshToken(oldRefreshToken);
            ProfileEntity user = profileRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // 3. Find the existing session ID from the database using the old token
            // This is critical so the Filter's existsById(sessionId) doesn't fail
            Session session = sessionRepository.findByRefreshToken(oldRefreshToken)
                    .orElseThrow(() -> new BadRequestException("Session expired or invalid"));

            // 4. Generate new tokens
            String newRefreshToken = jwtService.generateRefreshToken(user);

            // 5. Update the existing record in DB with the NEW refresh token string
            session.setRefreshToken(newRefreshToken);
            sessionRepository.save(session);

            // 6. Generate a new Access Token (which expires in 1 min) linked to the SAME session ID
            String accessToken = jwtService.generateAccessToken(user, session.getId());

            return Map.of(
                    "id", user.getId(),
                    "accessToken", accessToken,
                    "refreshToken", newRefreshToken,
                    "fullName", user.getFullName(),
                    "profileImageUrl", user.getProfileImageUrl() != null ? user.getProfileImageUrl() : ""
            );

        } catch (Exception ex) {
            log.error("Refresh token failed: {}", ex.getMessage());
            throw new BadRequestException("Session expired. Please login again.");
        }
    }

    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
