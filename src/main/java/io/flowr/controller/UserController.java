package io.flowr.controller;

import io.flowr.dto.user.InviteDto;
import io.flowr.dto.auth.PasswordDto;
import io.flowr.dto.common.ApiResponse;
import io.flowr.dto.user.ProfileDto;
import io.flowr.service.UserService;
import io.flowr.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
class UserController {
    private final UserService userService;

    /**
     * Get all users
     * GET /api/v1/users
     */
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
    public ResponseEntity<ApiResponse<List<ProfileDto.Response>>> getAllUsers() {
        try {
            List<ProfileDto.Response> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (RuntimeException e) {
            log.error("Failed to get all users: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get user by id
     * GET /api/v1/users/:id
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProfileDto.Response>> getUserById(@PathVariable UUID id) {
        try {
            ProfileDto.Response userInfo = userService.getCurrentUser(id);
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (RuntimeException e) {
            log.error("Failed to get user by id: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    /**
     * Assign/Change User Role ---> Admin only
     * PUT /api/v1/users/:id/assign
     */
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignRole(@PathVariable UUID id, @Valid @RequestBody ProfileDto.RoleRequest request) {
        try {
            userService.assignRole(id, request);
            return ResponseEntity.ok(ApiResponse.success("User role updated successfully", null));
        } catch (RuntimeException e) {
            log.error("Failed to assign role: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    /**
     * Get current user information
     * GET /api/v1/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileDto.Response>> getCurrentUser() {
        try {
            ProfileDto.Response userInfo = userService.getCurrentUser(SecurityUtils.getCurrentUserId());
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (RuntimeException e) {
            log.error("Failed to get current user: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Edit User Profile
     * PUT /api/v1/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> editProfile(@Valid @RequestBody ProfileDto.Request request) {
        try{
            UUID userId = SecurityUtils.getCurrentUserId();
            userService.editUserProfile(request, userId);
            return ResponseEntity.ok(ApiResponse.success("User profile edited successfully", null));
        }catch (RuntimeException e){
            log.error("Failed to edit user profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Change password endpoint
     * PUT /api/v1/user/change-password
     * Requires authentication
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordDto.ChangeRequest request) {
        try {
            userService.changePassword(request, SecurityUtils.getCurrentUserId());
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
        } catch (RuntimeException e) {
            log.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * User invitation endpoint
     * POST /api/v1/user/invite
     */
    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InviteDto.Response>> invite(@Valid @RequestBody InviteDto.Request request) {
        try {
            request.setOrganizationId(SecurityUtils.getCurrentUserOrganizationId());

            InviteDto.Response response = userService.inviteUser(request);
            return ResponseEntity.ok(ApiResponse.success("Invitation successful", response));
        } catch (RuntimeException e) {
            log.error("Invitation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}