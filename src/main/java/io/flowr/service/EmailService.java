package io.flowr.service;

/**
 * Email Service Interface
 * Implement this interface with an email provider
 * (e.g., SendGrid, AWS SES, SMTP, etc.)
 */
public interface EmailService {

    void sendEmailVerification(String email, String verificationToken);


    void sendPasswordResetEmail(String email, String resetToken);


    void sendWelcomeEmail(String email, String name);


    void sendPasswordChangeNotification(String email);
}