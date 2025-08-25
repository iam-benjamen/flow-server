package io.flowr.mapper;

import io.flowr.dto.workflow.WorkflowDetailsDto;
import io.flowr.entity.User;
import io.flowr.entity.Workflow;
import io.flowr.entity.WorkflowStep;
import io.flowr.entity.WorkflowStepAction;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface WorkflowDetailsMapper {

    @Mapping(target = "isActive", expression = "java(workflow.getIsActive())")
    @Mapping(target = "steps", expression = "java(mapSteps(workflow.getSteps(), workflow))")
    WorkflowDetailsDto.Response toResponse(Workflow workflow);

    @Mapping(target = "actions", source = "stepActions")
    @Mapping(target = "isCurrentStep", expression = "java(isCurrentStep(step, workflow.getCurrentStep()))")
    WorkflowDetailsDto.StepDetails toStepDetails(WorkflowStep step, @Context Workflow workflow);

    WorkflowDetailsDto.ActionDetails toActionDetails(WorkflowStepAction action);

    WorkflowDetailsDto.UserSummary toUserSummary(User user);

    default List<WorkflowDetailsDto.StepDetails> mapSteps(List<WorkflowStep> steps, Workflow workflow) {
        return steps.stream()
                .map(step -> toStepDetails(step, workflow))
                .collect(Collectors.toList());
    }

    default boolean isCurrentStep(WorkflowStep step, WorkflowStep currentStep) {
        return currentStep != null && currentStep.getId().equals(step.getId());
    }
}