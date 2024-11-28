package ch.sbb.polarion.extension.requirements_inspector.util.configurationstatus;

import ch.sbb.polarion.extension.generic.configuration.ConfigurationStatus;
import ch.sbb.polarion.extension.generic.configuration.ConfigurationStatusProvider;
import ch.sbb.polarion.extension.generic.configuration.Status;
import ch.sbb.polarion.extension.generic.settings.GenericNamedSettings;
import ch.sbb.polarion.extension.generic.settings.NamedSettingsRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsStatusProviderTest {

    @Mock
    private NamedSettingsRegistry namedSettingsRegistry;

    @Mock
    @SuppressWarnings("rawtypes")
    private GenericNamedSettings genericNamedSettings;

    @Test
    void testGetSettingsStatusReturnOkOnEmptySet() {
        when(namedSettingsRegistry.getAll()).thenReturn(Set.of());
        ConfigurationStatus configurationStatus = new SettingsStatusProvider(namedSettingsRegistry).getStatus(ConfigurationStatusProvider.Context.builder().scope("").build());
        assertEquals(new ConfigurationStatus("Default Settings", Status.OK, ""), configurationStatus);
    }

    @Test
    void testGetSettingsStatusReturnOkOnOneElementSet() {
        when(genericNamedSettings.getFeatureName()).thenReturn("abc");
        when(genericNamedSettings.readNames("")).thenReturn(Set.of());
        when(namedSettingsRegistry.getByFeatureName("abc")).thenReturn(genericNamedSettings);
        when(namedSettingsRegistry.getAll()).thenReturn(Set.of(genericNamedSettings));
        ConfigurationStatus configurationStatus = new SettingsStatusProvider(namedSettingsRegistry).getStatus(ConfigurationStatusProvider.Context.builder().scope("").build());
        assertEquals(new ConfigurationStatus("Default Settings", Status.OK, ""), configurationStatus);
    }

    @Test
    void testGetSettingsStatusReturnErrorOnException() {
        when(genericNamedSettings.getFeatureName()).thenReturn("abc");
        when(namedSettingsRegistry.getByFeatureName(anyString())).thenThrow(new RuntimeException(""));
        when(namedSettingsRegistry.getAll()).thenReturn(Set.of(genericNamedSettings));
        ConfigurationStatus configurationStatus = new SettingsStatusProvider(namedSettingsRegistry).getStatus(ConfigurationStatusProvider.Context.builder().scope("").build());
        assertEquals(new ConfigurationStatus("Default Settings", Status.ERROR, ""), configurationStatus);
    }
}