package ch.sbb.polarion.extension.requirements_inspector.rest.model;

import java.util.List;

public record InspectWorkItemsParams(
        String projectId,
        List<String> ids,
        List<String> inspectFields,
        boolean ignoreInspectTitle,
        boolean addMissingLanguage) {}