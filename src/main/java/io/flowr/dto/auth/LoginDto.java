package io.flowr.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class LoginDto {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
        
        @NotBlank(message = "Password is required")
        private String password;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String token;
        
        @Builder.Default
        private String tokenType = "Bearer";

        private UserInfo user;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class UserInfo {
            private UUID id;
            private String name;
            private String email;
            private String role;
            private UUID organizationId;
            private String organizationName;
        }
    }
}