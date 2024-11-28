package ch.sbb.polarion.extension.requirements_inspector;

import com.polarion.platform.core.IPlatform;
import com.polarion.platform.core.PlatformContext;
import com.polarion.platform.jobs.IJobDescriptor;
import com.polarion.platform.jobs.IJobUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class RequirementsInspectorJobUnitFactoryTest {

    @Test
    @SuppressWarnings({"unchecked"})
    void testJobParams() {
        try (MockedStatic<PlatformContext> platformContextMockedStatic = mockStatic(PlatformContext.class)) {
            IPlatform platform = mock(IPlatform.class);
            platformContextMockedStatic.when(PlatformContext::getPlatform).thenReturn(platform);
            when(platform.lookupService(any())).thenReturn(null);

            IJobDescriptor descriptor = new RequirementsInspectorJobUnitFactory().getJobDescriptor(mock(IJobUnit.class));
            Assertions.assertTrue(descriptor.getRootParameterGroup().getParameters().containsAll(
                    Arrays.asList("types", "addMissingLanguage", "inspectTitle", "inspectFields", "filter")));
        }
    }

}
