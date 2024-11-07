package ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RequirementsInspectorVersion(
        String python,
        @JsonProperty("polarion_requirements_inspector") String polarionRequirementsInspector,
        @JsonProperty("polarion_requirements_inspector_service") String polarionRequirementsInspectorService
) {
}
