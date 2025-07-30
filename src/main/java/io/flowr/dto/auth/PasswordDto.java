package io.flowr.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PasswordDto {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChangeRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;
        
        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ForgotRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResetRequest {
        @NotBlank(message = "Token is required")
        private String token;
        
        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;
    }
}