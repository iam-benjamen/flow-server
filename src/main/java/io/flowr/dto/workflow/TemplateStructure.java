package io.flowr.dto.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.flowr.utils.Enums;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateStructure {
    @Valid
    @NotEmpty(message = "Steps cannot be empty")
    private List<StepTemplate> steps;

    @Data
    public static class StepTemplate {
        @NotBlank(message = "Step name is required")
        private String name;

        private String description;

        @NotNull
        private UUID defaultAssignedUserId;

        @NotNull @Min(1)
        private Integer stepOrder;

        @Valid
        private List<ActionTemplate> actions = new ArrayList<>();
    }

    @Data
    public static class ActionTemplate {
        @NotBlank(message = "Action name is required")
        private String name;

        private String description;

        @NotNull
        private Enums.ActionType actionType;

        @NotNull @Min(1)
        private Integer actionOrder;

        @NotNull
        private Boolean isOptional = false;
    }
}


