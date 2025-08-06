package io.flowr.controller;

import io.flowr.dto.auth.InviteDto;
import io.flowr.dto.auth.LoginDto;
import io.flowr.dto.auth.PasswordDto;
import io.flowr.dto.auth.RegisterDto;
import io.flowr.dto.common.ApiResponse;
import io.flowr.service.AuthService;
import io.flowr.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
class UserController {
    private final AuthService authService;

    /**
     * Get current user information
     * GET /api/v1/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginDto.Response.UserInfo>> getCurrentUser() {
        try {
            LoginDto.Response.UserInfo userInfo = authService.getCurrentUser(SecurityUtils.getCurrentUserId());
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (RuntimeException e) {
            log.error("Failed to get current user: {}", e.getMessage());
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
            authService.changePassword(request, SecurityUtils.getCurrentUserId());
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

            InviteDto.Response response = authService.inviteUser(request);
            return ResponseEntity.ok(ApiResponse.success("Invitation successful", response));
        } catch (RuntimeException e) {
            log.error("Invitation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}