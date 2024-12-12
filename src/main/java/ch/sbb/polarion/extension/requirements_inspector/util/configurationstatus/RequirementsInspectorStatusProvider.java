package ch.sbb.polarion.extension.requirements_inspector.util.configurationstatus;

import ch.sbb.polarion.extension.generic.configuration.ConfigurationStatus;
import ch.sbb.polarion.extension.generic.configuration.ConfigurationStatusProvider;
import ch.sbb.polarion.extension.generic.configuration.Status;
import ch.sbb.polarion.extension.generic.util.Discoverable;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspectorServiceConnector;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.model.RequirementsInspectorVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;

@Discoverable
@SuppressWarnings("unused")
public class RequirementsInspectorStatusProvider extends ConfigurationStatusProvider {

    private static final String REQUIREMENTS_INSPECTOR_SERVICE_PYTHON_VERSION = "Python version in the docker image requirements-inspector-service";
    private static final String REQUIREMENTS_INSPECTOR_SERVICE_PYTHON_MODULE_VERSION = "python-requirements-inspector module version in the docker image requirements-inspector-service";
    private static final String REQUIREMENTS_INSPECTOR_SERVICE_VERSION = "Docker image version of requirements-inspector-service";

    private final RequirementsInspectorServiceConnector requirementsInspectorServiceConnector;

    public RequirementsInspectorStatusProvider() {
        this.requirementsInspectorServiceConnector = new RequirementsInspectorServiceConnector();
    }

    @VisibleForTesting
    RequirementsInspectorStatusProvider(RequirementsInspectorServiceConnector requirementsInspectorServiceConnector) {
        this.requirementsInspectorServiceConnector = requirementsInspectorServiceConnector;
    }

    @Override
    public @NotNull List<ConfigurationStatus> getStatuses(@NotNull ConfigurationStatusProvider.Context context) {
        try {
            RequirementsInspectorVersion requirementsInspectorVersion = requirementsInspectorServiceConnector.getRequirementsInspectorInfo();
            return List.of(
                    createRequirementsInspectorStatus(REQUIREMENTS_INSPECTOR_SERVICE_PYTHON_VERSION, requirementsInspectorVersion.python()),
                    createRequirementsInspectorStatus(REQUIREMENTS_INSPECTOR_SERVICE_PYTHON_MODULE_VERSION, requirementsInspectorVersion.polarionRequirementsInspector()),
                    createRequirementsInspectorStatus(REQUIREMENTS_INSPECTOR_SERVICE_VERSION, requirementsInspectorVersion.polarionRequirementsInspectorService())
            );
        } catch (Exception e) {
            if (e.getMessage() == null) {
                return List.of(new ConfigurationStatus(REQUIREMENTS_INSPECTOR_SERVICE_PYTHON_MODULE_VERSION, Status.ERROR, "Unknown"));
            }
            return List.of(new ConfigurationStatus(REQUIREMENTS_INSPECTOR_SERVICE_PYTHON_MODULE_VERSION, Status.ERROR, e.getMessage()));
        }
    }

    @VisibleForTesting
    ConfigurationStatus createRequirementsInspectorStatus(@NotNull String name, @Nullable String details) {
        if (details == null || details.isEmpty()) {
            return new ConfigurationStatus(name, Status.WARNING, "Unknown");
        }
        return new ConfigurationStatus(name, Status.OK, details);
    }
}
