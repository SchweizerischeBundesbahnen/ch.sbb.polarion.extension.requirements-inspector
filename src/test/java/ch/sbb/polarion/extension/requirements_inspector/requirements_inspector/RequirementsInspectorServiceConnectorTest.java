package ch.sbb.polarion.extension.requirements_inspector.requirements_inspector;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * The connector creates a client for each call, so it is the one which has to close it, also when the call fails.
 */
@ExtendWith(MockitoExtension.class)
class RequirementsInspectorServiceConnectorTest {

    @Mock
    MockedStatic<ClientBuilder> clientBuilderMockedStatic;
    @Mock
    MockedStatic<ServiceConnectorUtils> serviceConnectorUtilsMockedStatic;

    @Mock
    private Client client;
    private final RequirementsInspectorServiceConnector connector = new RequirementsInspectorServiceConnector();

    @BeforeEach
    void setUp() {
        clientBuilderMockedStatic.when(ClientBuilder::newClient).thenReturn(client);
    }

    @Test
    void inspectWorkItemsClosesTheClient() {
        List<Map<String, String>> input = List.of(Map.of("id", "EL-1"));
        List<Map<String, String>> output = List.of(Map.of("title", "Example"));
        serviceConnectorUtilsMockedStatic.when(() -> ServiceConnectorUtils.inspectWorkItems(input, client)).thenReturn(output);

        assertSame(output, connector.inspectWorkItems(input));
        verify(client).close();
    }

    @Test
    void inspectWorkItemsClosesTheClientWhenTheCallFails() {
        serviceConnectorUtilsMockedStatic.when(() -> ServiceConnectorUtils.inspectWorkItems(any(), any()))
                .thenThrow(new IllegalStateException("Could not get proper response from Requirements Inspector Service"));

        List<Map<String, String>> input = List.of();
        assertThrows(IllegalStateException.class, () -> connector.inspectWorkItems(input));
        verify(client).close();
    }

    @Test
    void getRequirementsInspectorInfoClosesTheClient() {
        RequirementsInspectorVersion version = new RequirementsInspectorVersion("3.11.9", "1.3.1", "1.0.0");
        serviceConnectorUtilsMockedStatic.when(() -> ServiceConnectorUtils.getRequirementsInspectorInfo(client)).thenReturn(version);

        assertSame(version, connector.getRequirementsInspectorInfo());
        verify(client).close();
    }

    @Test
    void getRequirementsInspectorInfoClosesTheClientWhenTheCallFails() {
        serviceConnectorUtilsMockedStatic.when(() -> ServiceConnectorUtils.getRequirementsInspectorInfo(any()))
                .thenThrow(new IllegalStateException("Could not parse response"));

        assertThrows(IllegalStateException.class, connector::getRequirementsInspectorInfo);
        verify(client).close();
    }
}
