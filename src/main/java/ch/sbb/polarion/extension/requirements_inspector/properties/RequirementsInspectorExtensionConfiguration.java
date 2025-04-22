package ch.sbb.polarion.extension.requirements_inspector.properties;

import ch.sbb.polarion.extension.generic.properties.CurrentExtensionConfiguration;
import ch.sbb.polarion.extension.generic.properties.ExtensionConfiguration;
import ch.sbb.polarion.extension.generic.util.Discoverable;
import com.polarion.core.config.impl.SystemValueReader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Discoverable
public final class RequirementsInspectorExtensionConfiguration extends ExtensionConfiguration {
    public static final String REQUIREMENTS_INSPECTOR_SERVICE = "requirements.inspector.service";
    public static final String REQUIREMENTS_INSPECTOR_SERVICE_DEFAULT = "http://localhost:9081";

    public static RequirementsInspectorExtensionConfiguration getInstance() {
        return (RequirementsInspectorExtensionConfiguration) CurrentExtensionConfiguration.getInstance().getExtensionConfiguration();
    }

    public String getRequirementsInspectorService() {
        return SystemValueReader.getInstance().readString(propertyPrefix + REQUIREMENTS_INSPECTOR_SERVICE, REQUIREMENTS_INSPECTOR_SERVICE_DEFAULT);
    }

    @Override
    public @NotNull List<String> getSupportedProperties() {
        List<String> supportedProperties = new ArrayList<>(super.getSupportedProperties());
        supportedProperties.add(REQUIREMENTS_INSPECTOR_SERVICE);
        return supportedProperties;
    }
}
