package io.flowr.dto.workflow;

import lombok.*;

import java.time.LocalDateTime;

public class ActionDataDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FileUpload {
        private String fileUrl;
        private String fileName;
        private String fileType; // pdf, jpg, etc.
        private Long fileSize;   // in bytes
        private LocalDateTime uploadedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Signature {
        private String signatureUrl;
        private String signerName;
        private String signerEmail;
        private LocalDateTime signedAt;
        private String signatureType; // digital, drawn, uploaded
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Review {
        private String comment;
        private Integer rating; // 1-5 stars, optional
        private String decision; // APPROVED, REJECTED, NEEDS_REVISION
        private LocalDateTime reviewedAt;
        private String reviewerNotes; // Additional internal notes
    }
}