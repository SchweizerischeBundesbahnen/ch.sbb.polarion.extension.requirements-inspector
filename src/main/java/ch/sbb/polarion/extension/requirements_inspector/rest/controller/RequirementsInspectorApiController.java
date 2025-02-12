package ch.sbb.polarion.extension.requirements_inspector.rest.controller;

import ch.sbb.polarion.extension.generic.rest.filter.Secured;
import ch.sbb.polarion.extension.requirements_inspector.service.PolarionService;
import lombok.NoArgsConstructor;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Secured
@Path("/api")
@NoArgsConstructor
public class RequirementsInspectorApiController extends RequirementsInspectorInternalController{
    private final PolarionService polarionService = new PolarionService();

    @Override
    public Response inspectRequirements(List<Map<String, String>> workItems) {
        return this.polarionService.callPrivileged(() -> super.inspectRequirements(workItems));
    }

    @Override
    public Response inspectRequirement(Map<String, String> workItem) {
        return this.polarionService.callPrivileged(() -> super.inspectRequirement(workItem));
    }
}
