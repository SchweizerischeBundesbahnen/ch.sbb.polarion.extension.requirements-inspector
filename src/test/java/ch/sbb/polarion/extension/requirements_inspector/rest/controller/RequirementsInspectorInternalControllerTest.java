package ch.sbb.polarion.extension.requirements_inspector.rest.controller;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspectorServiceConnector;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;
import ch.sbb.polarion.extension.requirements_inspector.rest.model.InspectWorkItemsParams;
import ch.sbb.polarion.extension.requirements_inspector.service.PolarionService;
import ch.sbb.polarion.extension.requirements_inspector.service.RequirementsInspectorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequirementsInspectorInternalControllerTest {

  @Mock RequirementsInspectorServiceConnector requirementsInspector;
  @Mock PolarionService polarionService;
  @Mock RequirementsInspectorService requirementsInspectorService;

  @InjectMocks RequirementsInspectorInternalController requirementsInspectorInternalController;

  @Test
  void testValidInputReturnsSuccess() {
    when(requirementsInspector.getRequirementsInspectorInfo())
            .thenReturn(new RequirementsInspectorVersion("3.12.7", "1.0.0", "1.0.0"));
    InspectWorkItemsParams inspectWorkItemsParams =
        new InspectWorkItemsParams("", List.of(), List.of(), false, true);
    when(requirementsInspectorService.inspectWorkItems(any(), any())).thenReturn(List.of());
    when(polarionService.getWorkItems(anyString(), any())).thenReturn(List.of());
    try (Response response =
        requirementsInspectorInternalController.inspectRequirements(inspectWorkItemsParams)) {
      assertThat(response.getStatus()).isEqualTo(200);
      assertThat(response.getEntity()).isEqualTo(List.of());
      assertThat(response.getHeaderString("python_version")).isEqualTo("3.12.7");
      assertThat(response.getHeaderString("polarion_requirements_inspector_version")).isEqualTo("1.0.0");
      assertThat(response.getHeaderString("polarion_requirements_inspector_service_version")).isEqualTo("1.0.0");
    }
  }

  @Test
  void testMissingParameterNullReturnsBadRequest() {
    assertThatThrownBy(
            () ->
                requirementsInspectorInternalController.inspectRequirements(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Inspection parameters should be provided");
  }

  @Test
  void testMissingParameterProjectIdReturnsBadRequest() {
    InspectWorkItemsParams inspectWorkItemsParams =
            new InspectWorkItemsParams(null, List.of(), List.of(), false, true);
    assertThatThrownBy(
            () ->
                    requirementsInspectorInternalController.inspectRequirements(inspectWorkItemsParams))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Parameter 'projectId' should be provided");
  }
}
