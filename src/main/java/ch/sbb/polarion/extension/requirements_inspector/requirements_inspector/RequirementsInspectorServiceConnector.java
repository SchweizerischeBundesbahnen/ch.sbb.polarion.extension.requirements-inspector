package ch.sbb.polarion.extension.requirements_inspector.requirements_inspector;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.List;
import java.util.Map;

public class RequirementsInspectorServiceConnector implements RequirementsInspector {
    @Override
    public List<Map<String, String>> inspectWorkitems(List<Map<String, String>> input) {
        Client client = ClientBuilder.newClient();
        return ServiceConnectorUtils.inspectWorkitems(input, client);
    }

    @Override
    public RequirementsInspectorVersion getRequirementsInspectorInfo() {
        Client client = ClientBuilder.newClient();
        return ServiceConnectorUtils.getRequirementsInspectorInfo(client);
    }
}
