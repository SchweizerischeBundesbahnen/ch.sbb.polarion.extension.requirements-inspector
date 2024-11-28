package ch.sbb.polarion.extension.requirements_inspector.requirements_inspector;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;
import ch.sbb.polarion.extension.requirements_inspector.configuration.RequirementsInspectorExtensionConfigurationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, RequirementsInspectorExtensionConfigurationExtension.class})
class ServiceConnectorUtilsTest {
    private static Client client = null;
    private static final List<Map<String, String>> workItems = List.of();

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
    void testInspectWorkitemsEmpty() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("[]");
        setupPost(response);

        assertEquals("[]", ServiceConnectorUtils.inspectWorkitems(workItems, client));
    }

    @Test
    void testInspectWorkitems() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("[{\"title\": \"Example\"}]");
        setupPost(response);

        assertNotNull(ServiceConnectorUtils.inspectWorkitems(workItems, client));
    }

    @Test
    void testInspectWorkitemsBadStatusCode() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(500);
        setupPost(response);

        assertThrows(IllegalStateException.class, () -> ServiceConnectorUtils.inspectWorkitems(workItems, client));
    }

    @Test
    void testGetRequirementsInspectorVersion() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{\"python\":\"3.11.9\", \"polarion_requirements_inspector\": \"1.3.1\", \"polarion_requirements_inspector_service\": \"1.0.0\"}");
        setupGet(response);

        assertEquals(new RequirementsInspectorVersion("3.11.9", "1.3.1", "1.0.0"), ServiceConnectorUtils.getRequirementsInspectorInfo(client));
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
    }
}
