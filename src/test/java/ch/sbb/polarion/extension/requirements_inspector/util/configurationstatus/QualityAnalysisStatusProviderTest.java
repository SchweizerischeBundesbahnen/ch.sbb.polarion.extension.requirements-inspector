package ch.sbb.polarion.extension.requirements_inspector.util.configurationstatus;

import ch.sbb.polarion.extension.generic.configuration.ConfigurationStatus;
import ch.sbb.polarion.extension.generic.configuration.ConfigurationStatusProvider;
import ch.sbb.polarion.extension.generic.configuration.Status;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspectorServiceConnector;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequirementsInspectorStatusProviderTest {

    @Mock
    private RequirementsInspectorServiceConnector requirementsInspectorServiceConnector;

    @Test
    void testGetRequirementsInspectorStatusReturnErrorStatusOnRequestError() {
        when(requirementsInspectorServiceConnector.getRequirementsInspectorInfo()).thenThrow(RuntimeException.class);
        List<ConfigurationStatus> configurationStatuses = new RequirementsInspectorStatusProvider(requirementsInspectorServiceConnector).getStatuses(ConfigurationStatusProvider.Context.builder().build());
        assertEquals(Status.ERROR, configurationStatuses.get(0).getStatus());
    }

    @Test
    void testGetRequirementsInspectorStatusReturnWarningStatusOnMissingStatus() {
        RequirementsInspectorVersion requirementsInspectorVersion = new RequirementsInspectorVersion(null, null, "");
        when(requirementsInspectorServiceConnector.getRequirementsInspectorInfo()).thenReturn(requirementsInspectorVersion);
        List<ConfigurationStatus> configurationStatuses = new RequirementsInspectorStatusProvider(requirementsInspectorServiceConnector).getStatuses(ConfigurationStatusProvider.Context.builder().build());

        assertEquals(Status.WARNING, configurationStatuses.get(0).getStatus());
        assertEquals(Status.WARNING, configurationStatuses.get(1).getStatus());
        assertEquals(Status.WARNING, configurationStatuses.get(2).getStatus());

        assertEquals("Unknown", configurationStatuses.get(0).getDetails());
        assertEquals("Unknown", configurationStatuses.get(1).getDetails());
        assertEquals("Unknown", configurationStatuses.get(2).getDetails());
    }

    @Test
    void testGetRequirementsInspectorStatusReturnOkStatusOnValidStatus() {
        RequirementsInspectorVersion requirementsInspectorVersion = new RequirementsInspectorVersion("3.11.9", "1.3.1", "1.0.0");
        when(requirementsInspectorServiceConnector.getRequirementsInspectorInfo()).thenReturn(requirementsInspectorVersion);
        List<ConfigurationStatus> configurationStatuses = new RequirementsInspectorStatusProvider(requirementsInspectorServiceConnector).getStatuses(ConfigurationStatusProvider.Context.builder().build());

        assertEquals(Status.OK, configurationStatuses.get(0).getStatus());
        assertEquals(Status.OK, configurationStatuses.get(1).getStatus());
        assertEquals(Status.OK, configurationStatuses.get(2).getStatus());

        assertEquals(requirementsInspectorVersion.python(), configurationStatuses.get(0).getDetails());
        assertEquals(requirementsInspectorVersion.polarionRequirementsInspector(), configurationStatuses.get(1).getDetails());
        assertEquals(requirementsInspectorVersion.polarionRequirementsInspectorService(), configurationStatuses.get(2).getDetails());
    }

    @Test
    void testCreateRequirementsInspectorStatusReturnOkOnValidStatus() {
        ConfigurationStatus configurationStatus = new RequirementsInspectorStatusProvider(requirementsInspectorServiceConnector).createRequirementsInspectorStatus("python", "1.3.1");
        assertEquals(new ConfigurationStatus("python", Status.OK, "1.3.1"), configurationStatus);
    }

    @Test
    void testCreateRequirementsInspectorStatusReturnWarningOnNullDetails() {
        ConfigurationStatus configurationStatus = new RequirementsInspectorStatusProvider(requirementsInspectorServiceConnector).createRequirementsInspectorStatus("python", null);
        assertEquals(new ConfigurationStatus("python", Status.WARNING, "Unknown"), configurationStatus);
    }

    @Test
    void testCreateRequirementsInspectorStatusReturnWarningOnEmptyDetails() {
        ConfigurationStatus configurationStatus = new RequirementsInspectorStatusProvider(requirementsInspectorServiceConnector).createRequirementsInspectorStatus("python", "");
        assertEquals(new ConfigurationStatus("python", Status.WARNING, "Unknown"), configurationStatus);
    }
}