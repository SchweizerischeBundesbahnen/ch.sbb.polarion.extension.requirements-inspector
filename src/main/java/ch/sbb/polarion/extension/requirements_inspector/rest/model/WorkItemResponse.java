package ch.sbb.polarion.extension.requirements_inspector.rest.model;

public record WorkItemResponse(
        String id,
        String language,
        String smellComplex,
        int smellPassive,
        int smellWeakword,
        int smellComparative,
        boolean missingProcessword,
        String smellDescription) {}
