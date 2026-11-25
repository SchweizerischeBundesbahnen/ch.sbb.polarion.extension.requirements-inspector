package ch.sbb.polarion.extension.requirements_inspector.rest.controller;

import ch.sbb.polarion.extension.generic.rest.filter.Secured;
import ch.sbb.polarion.extension.requirements_inspector.rest.model.InspectWorkItemsParams;
import ch.sbb.polarion.extension.requirements_inspector.service.RequirementsInspectorPolarionService;
import lombok.NoArgsConstructor;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Singleton
@Secured
@Path("/api")
@NoArgsConstructor
public class RequirementsInspectorApiController extends RequirementsInspectorInternalController {
    private final RequirementsInspectorPolarionService polarionService = new RequirementsInspectorPolarionService();

    @Override
    public Response inspectRequirements(InspectWorkItemsParams inspectWorkItemsParams) {
        return this.polarionService.callPrivileged(
                () -> super.inspectRequirements(inspectWorkItemsParams));
    }
}
