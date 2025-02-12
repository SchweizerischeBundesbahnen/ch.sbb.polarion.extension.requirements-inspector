package ch.sbb.polarion.extension.requirements_inspector.requirements_inspector;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;

import java.util.List;
import java.util.Map;

public interface RequirementsInspector {
    List<Map<String, String>> inspectWorkitems(List<Map<String, String>> input);
    @SuppressWarnings("unused")
    RequirementsInspectorVersion getRequirementsInspectorInfo();
}
