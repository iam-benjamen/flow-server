package io.flowr.controller;

import io.flowr.dto.auth.LoginDto;
import io.flowr.dto.auth.PasswordDto;
import io.flowr.dto.auth.RegisterDto;
import io.flowr.dto.common.ApiResponse;
import io.flowr.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
class AuthController {
    private final AuthService authService;

    /**
     * User Login endpoint
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDto.Response>> login(@Valid @RequestBody LoginDto.Request request) {
        try{
            LoginDto.Response response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e){
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * User registration endpoint
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterDto.Response>> register(@Valid @RequestBody RegisterDto.Request request) {
        try {
            RegisterDto.Response response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    /**
     * Forgot password endpoint
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody PasswordDto.ForgotRequest request) {
        try {
            authService.forgotPassword(request);
            return ResponseEntity.ok(ApiResponse.success(
                    "If an account with that email exists, we've sent a password reset link", null));
        } catch (RuntimeException e) {
            log.error("Forgot password failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Reset password endpoint
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PasswordDto.ResetRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.success("Password reset successful", null));
        } catch (RuntimeException e) {
            log.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Email verification endpoint
     * GET /api/v1/auth/verify-email?token=xyz
     */
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
        } catch (RuntimeException e) {
            log.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Accept Invitation Endpoint
     * GET /api/v1/auth/accept-invite?token=xyz
     */
    @GetMapping("/accept-invite")
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(@RequestParam String token) {
        try {
            authService.acceptInvitation(token);
            return ResponseEntity.ok(ApiResponse.success("Invitation accepted successfully", null));
        } catch (RuntimeException e) {
            log.error("Invitation acceptance failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Resend email verification endpoint
     * POST /api/v1/auth/resend-verification
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendEmailVerification(@Valid @RequestBody PasswordDto.ResendVerificationRequest request) {
        try {
            authService.resendEmailVerification(request.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Verification email sent", null));
        } catch (RuntimeException e) {
            log.error("Resend verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
