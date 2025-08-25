package io.flowr.mapper;

import io.flowr.dto.workflow.WorkflowDto;
import io.flowr.entity.User;
import io.flowr.entity.Workflow;
import io.flowr.entity.WorkflowStep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface WorkflowListMapper {

    @Mapping(target = "isActive", expression = "java(workflow.getIsActive())")
    @Mapping(target = "currentStep", source = "currentStep", qualifiedByName = "mapCurrentStep")
    @Mapping(target = "initiatedBy", source = "initiatedBy")
    WorkflowDto.Response toResponse(Workflow workflow);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "stepOrder", source = "stepOrder")
    WorkflowDto.WorkFlowStepSummary toStepSummary(WorkflowStep step);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    WorkflowDto.UserSummary toUserSummary(User user);

    @Named( "mapCurrentStep")
    default WorkflowDto.WorkFlowStepSummary mapCurrentStep(WorkflowStep currentStep) {
        return currentStep != null ? toStepSummary(currentStep) : null;
    }
}