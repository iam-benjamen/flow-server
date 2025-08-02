package io.flowr.controller;

import io.flowr.dto.auth.LoginDto;
import io.flowr.dto.common.ApiResponse;
import io.flowr.service.AuthService;
import io.flowr.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}