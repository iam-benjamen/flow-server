package io.flowr.service;

import io.flowr.dto.auth.PasswordDto;
import io.flowr.dto.user.InviteDto;
import io.flowr.dto.user.ProfileDto;
import io.flowr.entity.Organization;
import io.flowr.entity.User;
import io.flowr.repository.OrganizationRepository;
import io.flowr.repository.UserRepository;
import io.flowr.utils.Enums;
import io.flowr.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ProfileDto.Response getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ProfileDto.Response.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .isActive(user.getIsActive())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .organizationId(user.getOrganization().getId())
                .organizationName(user.getOrganization().getName())
                .build();
    }

    public void editUserProfile(ProfileDto.Request request, UUID userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setName(request.getName());
            user.setAvatarUrl(request.getAvatarUrl());
            userRepository.save(user);

            log.info("User profile edited successfully: {}", user.getEmail());
        } catch (Exception e) {
            log.error("failed to edit user profile: {}", e.getMessage());
            throw new RuntimeException("Failed to edit user profile");
        }
    }


    public void acceptInvitation(String token) {
        try {
            var claims = jwtService.validateAuthToken(token);

            String email = claims.getSubject() == null ? "" : claims.getSubject();
            String userId = claims.get("userId", String.class);
            String organizationId = claims.get("organizationId", String.class);

            User user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Organization organization = organizationRepository.findById(UUID.fromString(organizationId))
                    .orElseThrow(() -> new RuntimeException("Organization not found"));

            if (!user.getEmail().equals(email) || !organization.getId().equals(user.getOrganization().getId())) {
                throw new RuntimeException("Invalid invitation token");
            }

            if (user.getIsActive()) {
                throw new RuntimeException("Invitation already accepted");
            }

            user.setEmailVerified(true);
            user.setIsActive(true);
            userRepository.save(user);

            log.info("Invitation accepted successfully: {}", user.getEmail());

        } catch (Exception e) {
            log.error("failed to accept invitation: {}", e.getMessage());
            throw new RuntimeException("Invalid or expired invitation token");
        }
    }

    public void changePassword(PasswordDto.ChangeRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getEmail());
    }

    public InviteDto.Response inviteUser(InviteDto.Request request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalStateException("User with this email already exists");
        }

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        String temporaryPassword = UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(temporaryPassword))
                .role(request.getRole())
                .organization(organization)
                .isActive(false)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        String invitationToken = jwtService.generateToken(
                user.getEmail(),
                user.getId().toString(),
                user.getRole().name(),
                user.getOrganization().getId().toString()
        );

        log.info("User invited: {} by {}. Invitation Token: {}", user.getEmail(), SecurityUtils.getCurrentUserEmail(), invitationToken);

        return InviteDto.Response.builder()
                .message("Invitation sent successfully")
                .email(user.getEmail())
                .build();
    }

    public List<ProfileDto.Response> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> ProfileDto.Response.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .isActive(user.getIsActive())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .organizationId(user.getOrganization().getId())
                .organizationName(user.getOrganization().getName())
                .build()).toList();
    }

    public void assignRole(UUID id, ProfileDto.RoleRequest request) {
        if (id.equals(SecurityUtils.getCurrentUserId())) {
            throw new RuntimeException("Cannot update your own role");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID currentUserOrgId = SecurityUtils.getCurrentUserOrganizationId();

        log.info("Assigning role {} to user {} in organization {}", request.getRole(), user.getEmail(), currentUserOrgId);

        if (!user.getOrganization().getId().equals(currentUserOrgId)) {
            throw new RuntimeException("User does not belong to your organization");
        }

        try {
            Enums.Role newRole = Enums.Role.valueOf(request.getRole().toUpperCase());

            if (user.getRole() == newRole) {
                log.info("User already has role {}, skipping update", newRole);
                return;
            }

            user.setRole(newRole);
            userRepository.save(user);
            log.info("Role updated for user: {} to {}", user.getEmail(), newRole);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role provided: {}", request.getRole());
            throw new RuntimeException("Invalid role provided");
        } catch (Exception e) {
            log.error("Failed to assign role: {}", e.getMessage());
            throw new RuntimeException("Failed to assign role");
        }
    }
}
