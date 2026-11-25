package ch.sbb.polarion.extension.requirements_inspector.requirements_inspector;

import ch.sbb.polarion.extension.requirements_inspector.configuration.RequirementsInspectorExtensionConfigurationExtension;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, RequirementsInspectorExtensionConfigurationExtension.class})
class ServiceConnectorUtilsTest {
    private static final List<Map<String, String>> workItems = List.of();
    private static Client client = null;

    static void setupPost(Response response) {
        Builder builder = mock(Builder.class);
        when(builder.post(any())).thenReturn(response);

        WebTarget webTarget = mock(WebTarget.class);
        when(webTarget.request(anyString())).thenReturn(builder);

        client = mock(Client.class);
        when(client.target(anyString())).thenReturn(webTarget);
    }

    static void setupGet(Response response) {
        Builder builder = mock(Builder.class);
        when(builder.get()).thenReturn(response);

        WebTarget webTarget = mock(WebTarget.class);
        when(webTarget.request(anyString())).thenReturn(builder);

        client = mock(Client.class);
        when(client.target(anyString())).thenReturn(webTarget);
    }

    @Test
    void testInspectWorkItemsEmpty() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("[]");
        setupPost(response);

        assertEquals(List.of(), ServiceConnectorUtils.inspectWorkItems(workItems, client));
        // the client belongs to the caller, which may use it again
        verify(client, never()).close();
    }

    @Test
    void testInspectWorkItems() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("[{\"title\": \"Example\"}]");
        setupPost(response);

        assertNotNull(ServiceConnectorUtils.inspectWorkItems(workItems, client));
    }

    @Test
    void testInspectWorkItemsBadStatusCode() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(500);
        setupPost(response);

        assertThrows(IllegalStateException.class, () -> ServiceConnectorUtils.inspectWorkItems(workItems, client));
        verify(client, never()).close();
        verify(response).close();
    }

    @Test
    void testGetRequirementsInspectorVersion() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{\"python\":\"3.11.9\", \"polarion_requirements_inspector\": \"1.3.1\", \"polarion_requirements_inspector_service\": \"1.0.0\"}");
        setupGet(response);

        assertEquals(new RequirementsInspectorVersion("3.11.9", "1.3.1", "1.0.0"), ServiceConnectorUtils.getRequirementsInspectorInfo(client));
        verify(client, never()).close();
    }

    @Test
    void testGetRequirementsInspectorVersionInvalidJsonResponse() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{\"python\":\"3.11.9\", \"polarion_requirements_inspector\": \"1.3.1}");
        setupGet(response);

        assertThrows(IllegalStateException.class, () -> ServiceConnectorUtils.getRequirementsInspectorInfo(client));
    }

    @Test
    void testGetRequirementsInspectorVersionBadStatusCode() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(500);
        setupGet(response);

        assertThrows(IllegalStateException.class, () -> ServiceConnectorUtils.getRequirementsInspectorInfo(client));
        verify(client, never()).close();
        verify(response).close();
    }
}
