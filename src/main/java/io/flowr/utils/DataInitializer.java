package io.flowr.utils;

import io.flowr.entity.*;
import io.flowr.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Data Initializer
 * Creates initial test data when the application starts
 * Remove this in production and create proper data seeding
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final WorkflowTemplateRepository workflowTemplateRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowStepActionRepository workflowStepActionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (organizationRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        log.info("Initializing sample data...");

        // Create Organization
        Organization org = Organization.builder()
                .name("Acme Corp")
                .isActive(true)
                .build();
        org = organizationRepository.save(org);
        log.info("Created organization: {}", org.getName());

        // Create Admin User
        User admin = User.builder()
                .name("Admin User")
                .email("admin@acme.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Enums.Role.ADMIN)
                .organization(org)
                .isActive(true)
                .emailVerified(true)
                .build();
        admin = userRepository.save(admin);

        // Create Designer User
        User designer = User.builder()
                .name("Designer User")
                .email("designer@acme.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Enums.Role.DESIGNER)
                .organization(org)
                .isActive(true)
                .emailVerified(true)
                .build();
        designer = userRepository.save(designer);

        // Create Staff User
        User staff = User.builder()
                .name("Staff User")
                .email("staff@acme.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .role(Enums.Role.STAFF)
                .organization(org)
                .isActive(true)
                .emailVerified(true)
                .build();
        staff = userRepository.save(staff);

        log.info("Created {} users", 3);

        // Create Workflow Template
        String templateStructure = """
        {
          "steps": [
            {
              "name": "Document Collection",
              "description": "Collect required documents",
              "stepOrder": 1,
              "actions": [
                {
                  "name": "Upload ID",
                  "description": "Upload government issued ID",
                  "actionType": "FILE_UPLOAD",
                  "actionOrder": 1,
                  "isOptional": false
                },
                {
                  "name": "Upload Proof of Address",
                  "description": "Upload utility bill or bank statement",
                  "actionType": "FILE_UPLOAD",
                  "actionOrder": 2,
                  "isOptional": false
                }
              ]
            },
            {
              "name": "Manager Review",
              "description": "Manager reviews submitted documents",
              "stepOrder": 2,
              "actions": [
                {
                  "name": "Review Documents",
                  "description": "Check all uploaded documents",
                  "actionType": "APPROVAL",
                  "actionOrder": 1,
                  "isOptional": false
                },
                {
                  "name": "Background Check",
                  "description": "Perform background verification",
                  "actionType": "VERIFICATION",
                  "actionOrder": 2,
                  "isOptional": true
                }
              ]
            },
            {
              "name": "Final Approval",
              "description": "Final approval from admin",
              "stepOrder": 3,
              "actions": [
                {
                  "name": "Admin Approval",
                  "description": "Final approval decision",
                  "actionType": "APPROVAL",
                  "actionOrder": 1,
                  "isOptional": false
                }
              ]
            }
          ]
        }
        """;

        WorkFlowTemplate template = WorkFlowTemplate.builder()
                .title("Employee Onboarding")
                .description("Standard employee onboarding process")
                .defaultPriority(Enums.Priority.MEDIUM)
                .templateStructure(templateStructure)
                .isActive(true)
                .organization(org)
                .createdBy(admin)
                .build();
        template = workflowTemplateRepository.save(template);
        log.info("Created workflow template: {}", template.getTitle());

        // Create Workflow
        Workflow workflow = Workflow.builder()
                .title("John Doe Onboarding")
                .description("Onboarding process for John Doe")
                .status(Enums.WorkflowStatus.ACTIVE)
//                .isActive(.getIsActive())
                .priority(Enums.Priority.HIGH)
                .startedAt(LocalDateTime.now().minusDays(2))
                .dueAt(LocalDateTime.now().plusDays(5))
                .organization(org)
                .initiatedBy(designer)
                .build();
        workflow = workflowRepository.save(workflow);

        // Create Workflow Steps
        WorkflowStep step1 = WorkflowStep.builder()
                .name("Document Collection")
                .description("Collect required documents")
                .stepOrder(1)
                .status(Enums.StepStatus.COMPLETED)
                .assignedAt(LocalDateTime.now().minusDays(2))
                .completedAt(LocalDateTime.now().minusDays(1))
                .assignedTo(staff)
                .workflow(workflow)
                .build();
        step1 = workflowStepRepository.save(step1);

        WorkflowStep step2 = WorkflowStep.builder()
                .name("Manager Review")
                .description("Manager reviews submitted documents")
                .stepOrder(2)
                .status(Enums.StepStatus.IN_PROGRESS)
                .assignedAt(LocalDateTime.now().minusDays(1))
                .dueAt(LocalDateTime.now().plusDays(1))
                .assignedTo(designer)
                .workflow(workflow)
                .build();
        step2 = workflowStepRepository.save(step2);

        WorkflowStep step3 = WorkflowStep.builder()
                .name("Final Approval")
                .description("Final approval from admin")
                .stepOrder(3)
                .status(Enums.StepStatus.PENDING)
                .assignedTo(admin)
                .workflow(workflow)
                .build();
        step3 = workflowStepRepository.save(step3);

        // Set current step and update workflow
        workflow.setCurrentStep(step2);
        workflowRepository.save(workflow);

        // Create Step Actions for Step 1 with actionData
        WorkflowStepAction action1_1 = WorkflowStepAction.builder()
                .name("Upload ID")
                .description("Upload government issued ID")
                .actionType(Enums.ActionType.FILE_UPLOAD)
                .status(Enums.ActionStatus.COMPLETED)
                .actionOrder(1)
                .isOptional(false)
                .completedAt(LocalDateTime.now().minusDays(2))
                .actionData("""
                    {
                        "fileUrl": "https://storage.acme.com/uploads/john-doe-id.pdf",
                        "fileName": "john-doe-drivers-license.pdf",
                        "fileType": "pdf",
                        "fileSize": 2048576,
                        "uploadedAt": "2024-01-20T10:30:00"
                    }
                    """)
                .step(step1)
                .build();

        WorkflowStepAction action1_2 = WorkflowStepAction.builder()
                .name("Upload Proof of Address")
                .description("Upload utility bill or bank statement")
                .actionType(Enums.ActionType.FILE_UPLOAD)
                .status(Enums.ActionStatus.COMPLETED)
                .actionOrder(2)
                .isOptional(false)
                .completedAt(LocalDateTime.now().minusDays(1))
                .actionData("""
                    {
                        "fileUrl": "https://storage.acme.com/uploads/john-doe-utility-bill.pdf",
                        "fileName": "utility-bill-december-2024.pdf",
                        "fileType": "pdf",
                        "fileSize": 1536000,
                        "uploadedAt": "2024-01-21T14:15:00"
                    }
                    """)
                .step(step1)
                .build();

        workflowStepActionRepository.saveAll(Arrays.asList(action1_1, action1_2));

        // Create Step Actions for Step 2 with actionData
        WorkflowStepAction action2_1 = WorkflowStepAction.builder()
                .name("Review Documents")
                .description("Check all uploaded documents")
                .actionType(Enums.ActionType.REVIEW)
                .status(Enums.ActionStatus.IN_PROGRESS)
                .actionOrder(1)
                .isOptional(false)
                .actionData("""
                    {
                        "comment": "Documents look good so far, verifying details",
                        "rating": null,
                        "decision": "IN_REVIEW",
                        "reviewedAt": null,
                        "reviewerNotes": "ID document is clear and valid. Address verification pending."
                    }
                    """)
                .step(step2)
                .build();

        WorkflowStepAction action2_2 = WorkflowStepAction.builder()
                .name("Background Check")
                .description("Perform background verification")
                .actionType(Enums.ActionType.REVIEW)
                .status(Enums.ActionStatus.PENDING)
                .actionOrder(2)
                .isOptional(true)
                .actionData("""
                    {
                        "comment": "",
                        "rating": null,
                        "decision": "PENDING",
                        "reviewedAt": null,
                        "reviewerNotes": "Background check not yet initiated"
                    }
                    """)
                .step(step2)
                .build();

        workflowStepActionRepository.saveAll(Arrays.asList(action2_1, action2_2));

        // Create Step Actions for Step 3 with actionData
        WorkflowStepAction action3_1 = WorkflowStepAction.builder()
                .name("Admin Approval")
                .description("Final approval decision")
                .actionType(Enums.ActionType.REVIEW)
                .status(Enums.ActionStatus.PENDING)
                .actionOrder(1)
                .isOptional(false)
                .actionData("""
                    {
                        "comment": "",
                        "rating": null,
                        "decision": "PENDING",
                        "reviewedAt": null,
                        "reviewerNotes": "Awaiting manager review completion"
                    }
                    """)
                .step(step3)
                .build();

        workflowStepActionRepository.save(action3_1);

        log.info("Sample data initialized successfully!");
        log.info("Test users created:");
        log.info("  Admin: admin@acme.com / password123");
        log.info("  Designer: designer@acme.com / password123");
        log.info("  Staff: staff@acme.com / password123");
    }
}