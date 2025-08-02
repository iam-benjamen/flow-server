package io.flowr.utils;

import io.flowr.entity.Organization;
import io.flowr.utils.Role;
import io.flowr.entity.User;
import io.flowr.repository.OrganizationRepository;
import io.flowr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Initializer
 *
 * Creates initial test data when the application starts
 * Remove this in production and create proper data seeding
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeTestData();
    }

    private void initializeTestData() {
        // Skip if data already exists
        if (organizationRepository.count() > 0) {
            log.info("Test data already exists, skipping initialization");
            return;
        }

        log.info("Creating test data...");

        // Create test organization
        Organization org = Organization.builder()
                .name("Acme Corporation")
                .isActive(true)
                .build();

        org = organizationRepository.save(org);
        log.info("Created organization: {}", org.getName());

        // Create admin user
        User admin = User.builder()
                .name("Admin User")
                .email("admin@acme.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Role.ADMIN)
                .organization(org)
                .isActive(true)
                .emailVerified(true)
                .build();

        userRepository.save(admin);
        log.info("Created admin user: {}", admin.getEmail());

        // Create designer user
        User designer = User.builder()
                .name("Designer User")
                .email("designer@acme.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Role.DESIGNER)
                .organization(org)
                .isActive(true)
                .emailVerified(true)
                .build();

        userRepository.save(designer);
        log.info("Created designer user: {}", designer.getEmail());

        // Create staff user
        User staff = User.builder()
                .name("Staff User")
                .email("staff@acme.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Role.STAFF)
                .organization(org)
                .isActive(true)
                .emailVerified(true)
                .build();

        userRepository.save(staff);
        log.info("Created staff user: {}", staff.getEmail());

        log.info("Test data initialization completed!");
        log.info("You can now login with:");
        log.info("  Admin: admin@acme.com / password123");
        log.info("  Designer: designer@acme.com / password123");
        log.info("  Staff: staff@acme.com / password123");
    }
}