package ch.sbb.polarion.extension.requirements_inspector.rest.controller;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspector;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspectorServiceConnector;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;
import ch.sbb.polarion.extension.requirements_inspector.rest.model.InspectWorkItemParams;
import ch.sbb.polarion.extension.requirements_inspector.rest.model.InspectWorkItemsParams;
import ch.sbb.polarion.extension.requirements_inspector.rest.model.WorkItemResponse;
import ch.sbb.polarion.extension.requirements_inspector.service.PolarionService;
import ch.sbb.polarion.extension.requirements_inspector.service.RequirementsInspectorService;
import com.polarion.alm.tracker.model.IWorkItem;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Hidden
@Path("/internal")
@Tag(name = "Requirements Inspection")
@SuppressWarnings("java:S1200")
public class RequirementsInspectorInternalController {

  private final PolarionService polarionService = new PolarionService();
  private final RequirementsInspector requirementsInspector =
      new RequirementsInspectorServiceConnector();
  private final RequirementsInspectorService requirementsInspectorService;

  public RequirementsInspectorInternalController() {
    this.requirementsInspectorService =
        new RequirementsInspectorService(this.polarionService, this.requirementsInspector);
  }

  @POST
  @Path("/inspect/workitems")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Inspect multiple workitems",
      requestBody =
          @RequestBody(
              description = "List of workitems to inspect",
              required = true,
              content =
                  @Content(
                      schema =
                          @Schema(implementation = InspectWorkItemsParams.class, type = "object"),
                      mediaType = MediaType.APPLICATION_JSON)),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Return value with smell descriptions/counts",
            content =
                @Content(
                    array =
                        @ArraySchema(
                            schema =
                                @Schema(implementation = WorkItemResponse.class, type = "object")),
                    mediaType = MediaType.APPLICATION_JSON),
            headers = {
              @Header(
                  name = "python_version",
                  description = "Version of requirements-inspector service python",
                  schema = @Schema(implementation = String.class, type = "string")),
              @Header(
                  name = "polarion_requirements_inspector_version",
                  description = "Version of requirements-inspector",
                  schema = @Schema(implementation = String.class, type = "string")),
              @Header(
                  name = "polarion_requirements_inspector_service_version",
                  description = "Version of requirements-inspector service",
                  schema = @Schema(implementation = String.class, type = "string"))
            })
      })
  public Response inspectRequirements(InspectWorkItemsParams inspectWorkItemsParams) {
    RequirementsInspectorVersion version =
        this.requirementsInspector.getRequirementsInspectorInfo();
    List<IWorkItem> workItems =
        this.polarionService.getWorkItems(
            inspectWorkItemsParams.projectId(), inspectWorkItemsParams.ids());
    RequirementsInspectorService.Context context =
        this.requirementsInspectorService.getContext(
            inspectWorkItemsParams.ignoreInspectTitle(),
            inspectWorkItemsParams.addMissingLanguage(),
            inspectWorkItemsParams.addFields());
    List<Map<String, String>> out =
        this.requirementsInspectorService.inspectWorkItems(workItems, context);
    return Response.ok(out)
        .header("python_version", version.python())
        .header("polarion_requirements_inspector_version", version.polarionRequirementsInspector())
        .header(
            "polarion_requirements_inspector_service_version",
            version.polarionRequirementsInspectorService())
        .build();
  }

  @POST
  @Path("/inspect/workitem")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Inspect a single workitem",
      requestBody =
          @RequestBody(
              description = "Workitem to inspect",
              required = true,
              content =
                  @Content(
                      schema =
                          @Schema(implementation = InspectWorkItemParams.class, type = "object"),
                      mediaType = MediaType.APPLICATION_JSON)),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Return value with smell descriptions/counts",
            content =
                @Content(
                    schema = @Schema(implementation = WorkItemResponse.class, type = "object"),
                    mediaType = MediaType.APPLICATION_JSON),
            headers = {
              @Header(
                  name = "python_version",
                  description = "Version of requirements-inspector service python",
                  schema = @Schema(implementation = String.class, type = "string")),
              @Header(
                  name = "polarion_requirements_inspector_version",
                  description = "Version of requirements-inspector",
                  schema = @Schema(implementation = String.class, type = "string")),
              @Header(
                  name = "polarion_requirements_inspector_service_version",
                  description = "Version of requirements-inspector service",
                  schema = @Schema(implementation = String.class, type = "string"))
            })
      })
  public Response inspectRequirement(InspectWorkItemParams inspectWorkItemParams) {
    IWorkItem workItem =
        this.polarionService.getWorkItem(
            inspectWorkItemParams.projectId(), inspectWorkItemParams.id());
    RequirementsInspectorService.Context context =
        this.requirementsInspectorService.getContext(
            inspectWorkItemParams.ignoreInspectTitle(),
            inspectWorkItemParams.addMissingLanguage(),
            inspectWorkItemParams.addFields());
    List<Map<String, String>> out =
        this.requirementsInspectorService.inspectWorkItems(List.of(workItem), context);
    RequirementsInspectorVersion version =
        this.requirementsInspector.getRequirementsInspectorInfo();
    return !out.isEmpty()
        ? Response.ok(out.get(0))
            .header("python_version", version.python())
            .header(
                "polarion_requirements_inspector_version", version.polarionRequirementsInspector())
            .header(
                "polarion_requirements_inspector_service_version",
                version.polarionRequirementsInspectorService())
            .build()
        : Response.ok().build();
  }
}
