package io.flowr.service;

import io.flowr.dto.user.InviteDto;
import io.flowr.dto.auth.LoginDto;
import io.flowr.dto.auth.PasswordDto;
import io.flowr.dto.auth.RegisterDto;
import io.flowr.entity.Organization;
import io.flowr.entity.User;
import io.flowr.repository.OrganizationRepository;
import io.flowr.repository.UserRepository;
import io.flowr.utils.Enums;
import io.flowr.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginDto.Response login(LoginDto.Request request) {
        log.info("login request: {}", request);
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getEmailVerified()) {
                throw new RuntimeException("Email not verified. Please check your email for verification link.");
            }

            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getId().toString(),
                    user.getRole().name(),
                    user.getOrganization().getId().toString()
            );

            LoginDto.Response.UserInfo userInfo = LoginDto.Response.UserInfo.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .organizationId(user.getOrganization().getId())
                    .organizationName(user.getOrganization().getName())
                    .build();

            log.info("User {} logged in successfully", user.getEmail());

            return LoginDto.Response.builder()
                    .token(token)
                    .user(userInfo)
                    .build();
        } catch (AuthenticationException e){
            log.warn("Login failed for email: {}", request.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
    }


    public RegisterDto.Response register(RegisterDto.Request request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Enums.Role.STAFF)
                .organization(organization)
                .isActive(true)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        String verificationToken = jwtService.generateEmailVerificationToken(
                user.getEmail(),
                user.getId().toString()
        );

        log.info("New user registered: {},{}", user.getEmail(), verificationToken);

        // Send verification email

        log.info("New user registered: {}", user.getEmail());

        return RegisterDto.Response.builder()
                .message("Registration successful. Please check your email to verify your account.")
                .email(user.getEmail())
                .build();
    }


    public void verifyEmail(String token) {
        try {
            var claims = jwtService.validateEmailVerificationToken(token);
            String email = claims.get("email", String.class);
            String userId = claims.get("userId", String.class);

            User user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getEmail().equals(email)) {
                throw new RuntimeException("Invalid verification token");
            }

            if (user.getEmailVerified()) {
                throw new RuntimeException("Email already verified");
            }

            user.setEmailVerified(true);
            userRepository.save(user);

            log.info("Email verified successfully for user: {}", user.getEmail());

        } catch (Exception e) {
            log.error("Email verification failed: {}", e.getMessage());
            throw new RuntimeException("Invalid or expired verification token");
        }
    }

    public void forgotPassword(PasswordDto.ForgotRequest request) {
        Optional<User> userOpt = userRepository.findByEmailAndIsActiveTrue(request.getEmail());

        if (userOpt.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", request.getEmail());
            return;
        }

        User user = userOpt.get();

        String resetToken = jwtService.generatePasswordResetToken(
                user.getEmail(),
                user.getId().toString()
        );

        // Send password reset email

        log.info("Password reset token generated for user: {},{}", user.getEmail(), resetToken);
    }


    public void resetPassword(PasswordDto.ResetRequest request) {
        try {
            Claims claims = jwtService.validatePasswordResetToken(request.getToken());
            String email = claims.get("email", String.class);
            String userId = claims.get("userId", String.class);

            User user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getEmail().equals(email)) {
                throw new RuntimeException("Invalid reset token");
            }

            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            log.info("Password reset successfully for user: {}", user.getEmail());

        } catch (Exception e) {
            log.error("Password reset failed: {}", e.getMessage());
            throw new RuntimeException("Invalid or expired reset token");
        }
    }


    public void resendEmailVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmailVerified()) {
            throw new RuntimeException("Email already verified");
        }

        String verificationToken = jwtService.generateEmailVerificationToken(
                user.getEmail(),
                user.getId().toString()
        );

        // Send verification email

        log.info("Email verification resent for user: {}{}", user.getEmail(), verificationToken);
    }

}
