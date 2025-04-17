package ch.sbb.polarion.extension.requirements_inspector.rest;

import ch.sbb.polarion.extension.generic.rest.GenericRestApplication;
import ch.sbb.polarion.extension.requirements_inspector.rest.controller.RequirementsInspectorApiController;
import ch.sbb.polarion.extension.requirements_inspector.rest.controller.RequirementsInspectorInternalController;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RequirementsInspectorRestApplication extends GenericRestApplication {
    @Override
    protected @NotNull Set<Object> getExtensionControllerSingletons() {
        return Set.of(
                new RequirementsInspectorInternalController(),
                new RequirementsInspectorApiController()
        );
    }
}
