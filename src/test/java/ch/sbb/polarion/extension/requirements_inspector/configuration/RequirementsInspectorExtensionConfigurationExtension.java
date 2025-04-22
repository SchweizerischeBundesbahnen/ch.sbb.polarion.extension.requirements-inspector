package ch.sbb.polarion.extension.requirements_inspector.configuration;

import ch.sbb.polarion.extension.requirements_inspector.properties.RequirementsInspectorExtensionConfiguration;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.mockStatic;

public class RequirementsInspectorExtensionConfigurationExtension implements BeforeEachCallback, AfterEachCallback {
    private static RequirementsInspectorExtensionConfiguration requirementsInspectorExtensionConfiguration;
    private MockedStatic<RequirementsInspectorExtensionConfiguration> requirementsInspectorExtensionConfigurationMockedStatic;

    @SuppressWarnings({"unused"})
    public static void setRequirementsInspectorExtensionConfigurationMock(RequirementsInspectorExtensionConfiguration mock) {
        requirementsInspectorExtensionConfiguration = mock;
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        if (requirementsInspectorExtensionConfiguration == null) {
            requirementsInspectorExtensionConfiguration = Mockito.mock(RequirementsInspectorExtensionConfiguration.class);
        }

        requirementsInspectorExtensionConfigurationMockedStatic = mockStatic(RequirementsInspectorExtensionConfiguration.class);
        requirementsInspectorExtensionConfigurationMockedStatic.when(RequirementsInspectorExtensionConfiguration::getInstance).thenReturn(requirementsInspectorExtensionConfiguration);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        if (requirementsInspectorExtensionConfigurationMockedStatic != null) {
            requirementsInspectorExtensionConfigurationMockedStatic.close();
        }
        requirementsInspectorExtensionConfiguration = null;
    }
}
