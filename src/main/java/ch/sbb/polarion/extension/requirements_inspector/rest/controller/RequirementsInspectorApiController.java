package ch.sbb.polarion.extension.requirements_inspector.rest.controller;

import ch.sbb.polarion.extension.generic.rest.filter.Secured;
import ch.sbb.polarion.extension.requirements_inspector.rest.model.InspectWorkItemsParams;
import ch.sbb.polarion.extension.requirements_inspector.service.PolarionService;
import lombok.NoArgsConstructor;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Secured
@Path("/api")
@NoArgsConstructor
public class RequirementsInspectorApiController extends RequirementsInspectorInternalController {
    private final PolarionService polarionService = new PolarionService();

    @Override
    public Response inspectRequirements(InspectWorkItemsParams inspectWorkItemsParams) {
        return this.polarionService.callPrivileged(
                () -> super.inspectRequirements(inspectWorkItemsParams));
    }
}
