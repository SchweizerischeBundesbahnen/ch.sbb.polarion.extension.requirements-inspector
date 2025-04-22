package ch.sbb.polarion.extension.requirements_inspector.requirements_inspector;

import ch.sbb.polarion.extension.requirements_inspector.properties.RequirementsInspectorExtensionConfiguration;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;
import ch.sbb.polarion.extension.requirements_inspector.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public final class ServiceConnectorUtils {

    private ServiceConnectorUtils() {
    }

    public static List<Map<String, String>> inspectWorkitems(List<Map<String, String>> input, Client client) {
        WebTarget webTarget = client.target(RequirementsInspectorExtensionConfiguration.getInstance().getRequirementsInspectorService() + "/inspect/workitems");
        try {
            try (Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(JsonUtil.writeInputJson(input), MediaType.APPLICATION_JSON_TYPE))) {
                if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                    throw new IllegalStateException("Could not get proper response from Requirements Inspector Service");
                }
                String content = response.readEntity(String.class);
                return JsonUtil.parseOutputJson(content);
            }
        } finally {
            client.close();
        }
    }

    public static RequirementsInspectorVersion getRequirementsInspectorInfo(Client client) {
        WebTarget webTarget = client.target(RequirementsInspectorExtensionConfiguration.getInstance().getRequirementsInspectorService() + "/version");
        try {

            try (Response response = webTarget.request(MediaType.APPLICATION_JSON).get()) {
                if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                    throw new IllegalStateException("Could not get proper response from Requirements Inspector Service");
                }
                return new ObjectMapper().readValue(response.readEntity(String.class), RequirementsInspectorVersion.class);

            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Could not parse response", e);
            }
        } finally {
            client.close();
        }
    }
}
