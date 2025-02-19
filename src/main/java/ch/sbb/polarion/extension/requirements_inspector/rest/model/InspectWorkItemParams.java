package ch.sbb.polarion.extension.requirements_inspector.rest.model;

import java.util.List;

public record InspectWorkItemParams(
        String projectId,
        String id,
        List<String> addFields,
        boolean ignoreInspectTitle,
        boolean addMissingLanguage) {}