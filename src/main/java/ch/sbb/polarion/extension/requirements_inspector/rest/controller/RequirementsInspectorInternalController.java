package ch.sbb.polarion.extension.requirements_inspector.rest.controller;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspector;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspectorServiceConnector;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NoArgsConstructor;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
@NoArgsConstructor
public class RequirementsInspectorInternalController {

    private final RequirementsInspector requirementsInspectorServiceConnector =
            new RequirementsInspectorServiceConnector();

    @POST
    @Path("/inspect/requirements")
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
                            array =
                            @ArraySchema(
                                    schema = @Schema(implementation = WorkItem.class, type = "object")),
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
                                            schema = @Schema(
                                                    implementation = String.class,
                                                    type = "string"
                                            )
                                    ),
                                    @Header(
                                            name = "polarion_requirements_inspector_version",
                                            description = "Version of requirements-inspector",
                                            schema = @Schema(
                                                    implementation = String.class,
                                                    type = "string"
                                            )
                                    ),
                                    @Header(
                                            name = "polarion_requirements_inspector_service_version",
                                            description = "Version of requirements-inspector service",
                                            schema = @Schema(
                                                    implementation = String.class,
                                                    type = "string"
                                            )
                                    )
                            })
            })
    public Response inspectRequirements(List<Map<String, String>> workItems) {
        List<Map<String, String>> out = this.requirementsInspectorServiceConnector.inspectWorkitems(workItems);
        RequirementsInspectorVersion version =
                this.requirementsInspectorServiceConnector.getRequirementsInspectorInfo();
        return Response.ok(out)
                .header("python_version", version.python())
                .header("polarion_requirements_inspector_version", version.polarionRequirementsInspector())
                .header("polarion_requirements_inspector_service_version", version.polarionRequirementsInspectorService())
                .build();
    }

    @POST
    @Path("/inspect/requirement")
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
                            schema = @Schema(implementation = WorkItem.class, type = "object"),
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
                                            schema = @Schema(
                                                    implementation = String.class,
                                                    type = "string"
                                            )
                                    ),
                                    @Header(
                                            name = "polarion_requirements_inspector_version",
                                            description = "Version of requirements-inspector",
                                            schema = @Schema(
                                                    implementation = String.class,
                                                    type = "string"
                                            )
                                    ),
                                    @Header(
                                            name = "polarion_requirements_inspector_service_version",
                                            description = "Version of requirements-inspector service",
                                            schema = @Schema(
                                                    implementation = String.class,
                                                    type = "string"
                                            )
                                    )
                            })
            })
    public Response inspectRequirement(Map<String, String> workItem) {
        List<Map<String, String>> out = this.requirementsInspectorServiceConnector.inspectWorkitems(List.of(workItem));
        RequirementsInspectorVersion version =
                this.requirementsInspectorServiceConnector.getRequirementsInspectorInfo();
    return !out.isEmpty()
        ? Response.ok(out.get(0))
            .header("python_version", version.python())
            .header("polarion_requirements_inspector_version", version.polarionRequirementsInspector())
            .header("polarion_requirements_inspector_service_version", version.polarionRequirementsInspectorService())
            .build()
        : Response.serverError()
            .build();
    }

    @Path("/inspector/version")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get the requirement-inspector's versions",
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = RequirementsInspectorVersion.class,
                                    type = "object"
                            ),
                            mediaType = MediaType.APPLICATION_JSON
                    )
            )
    )
    public Response getVersion() {
        return Response.ok(this.requirementsInspectorServiceConnector.getRequirementsInspectorInfo()).build();
    }

    private record WorkItem(String id, String title, String language) {
    }

    private record WorkItemResponse(
            String id,
            String language,
            String smellComplex,
            int smellPassive,
            int smellWeakword,
            int smellComparative,
            boolean missingProcessword,
            String smellDescription) {
    }
}
