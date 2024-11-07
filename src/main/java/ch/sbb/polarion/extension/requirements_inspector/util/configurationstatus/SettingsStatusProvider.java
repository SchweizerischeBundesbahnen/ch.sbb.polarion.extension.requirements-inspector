package ch.sbb.polarion.extension.requirements_inspector.util.configurationstatus;

import ch.sbb.polarion.extension.generic.configuration.ConfigurationStatus;
import ch.sbb.polarion.extension.generic.configuration.ConfigurationStatusProvider;
import ch.sbb.polarion.extension.generic.configuration.Status;
import ch.sbb.polarion.extension.generic.settings.GenericNamedSettings;
import ch.sbb.polarion.extension.generic.settings.NamedSettingsRegistry;
import ch.sbb.polarion.extension.generic.util.Discoverable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

@Discoverable
@SuppressWarnings("unused")
public class SettingsStatusProvider extends ConfigurationStatusProvider {

    public static final String DEFAULT_SETTINGS = "Default Settings";

    private final NamedSettingsRegistry namedSettingsRegistry;

    public SettingsStatusProvider() {
        this.namedSettingsRegistry = NamedSettingsRegistry.INSTANCE;
    }

    @VisibleForTesting
    SettingsStatusProvider(NamedSettingsRegistry namedSettingsRegistry) {
        this.namedSettingsRegistry = namedSettingsRegistry;
    }

    @Override
    public @NotNull ConfigurationStatus getStatus(@NotNull ConfigurationStatusProvider.Context context) {
        ConfigurationStatus configurationStatus = new ConfigurationStatus(DEFAULT_SETTINGS, Status.OK, "");
        namedSettingsRegistry.getAll().stream()
                .map(GenericNamedSettings::getFeatureName)
                .forEach(featureName -> {
                    try {
                        namedSettingsRegistry.getByFeatureName(featureName).readNames(context.getScope());
                    } catch (Exception e) {
                        configurationStatus.setStatus(Status.ERROR);
                        configurationStatus.setDetails(e.getMessage());
                    }
                });
        return configurationStatus;
    }
}
