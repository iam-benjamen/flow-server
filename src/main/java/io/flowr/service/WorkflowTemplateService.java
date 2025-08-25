package io.flowr.service;

import io.flowr.dto.workflow.TemplateStructure;
import io.flowr.entity.WorkFlowTemplate;
import io.flowr.utils.JsonUtils;
import org.springframework.stereotype.Service;

@Service
public class WorkflowTemplateService {
    private final JsonUtils jsonUtils;

    public WorkflowTemplateService(JsonUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    public void createTemplate(String title, TemplateStructure structure) {
        WorkFlowTemplate template = new WorkFlowTemplate();
        template.setTitle(title);
        template.setTemplateStructure(jsonUtils.toJson(structure));
    }
}