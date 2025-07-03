package ch.sbb.polarion.extension.requirements_inspector;

import ch.sbb.polarion.extension.generic.util.JobLogger;
import ch.sbb.polarion.extension.requirements_inspector.service.PolarionService;
import ch.sbb.polarion.extension.requirements_inspector.service.RequirementsInspectorService;
import com.polarion.alm.projects.IProjectService;
import com.polarion.alm.projects.model.IProject;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.platform.context.IContext;
import com.polarion.platform.jobs.IJob;
import com.polarion.platform.jobs.IJobStatus;
import com.polarion.platform.jobs.IJobUnitFactory;
import com.polarion.platform.jobs.IProgressMonitor;
import com.polarion.platform.persistence.model.IPObjectList;
import com.polarion.subterra.base.data.identification.IContextId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequirementsInspectorJobUnitTest {

    @Test
    void jobRunTest() {
        JobLogger.getInstance().clear();

        PolarionService polarionService = mock(PolarionService.class);

        IProjectService projectService = mock(IProjectService.class);
        when(polarionService.getProjectService()).thenReturn(projectService);
        when(projectService.getProjectForContextId(any())).thenReturn(mock(IProject.class));

        ITrackerService trackerService = mock(ITrackerService.class);
        when(polarionService.getTrackerService()).thenReturn(trackerService);
        when(trackerService.queryWorkItems(any(), any(), any())).thenReturn(mock(IPObjectList.class));

        try (MockedConstruction<RequirementsInspectorService> mockedInspectorService = Mockito.mockConstruction(RequirementsInspectorService.class, (mock, context) -> {
        })) {

            RequirementsInspectorJobUnit jobUnit = new RequirementsInspectorJobUnit("testJobName", mock(IJobUnitFactory.class), polarionService);
            IProgressMonitor progressMonitor = mock(IProgressMonitor.class);

            jobUnit.setJob(mock(IJob.class));

            IContext scope = mock(IContext.class);
            IContextId contextId = mock(IContextId.class);
            when(scope.getId()).thenReturn(contextId);
            jobUnit.setScope(scope);

            // no types set
            IJobStatus status = jobUnit.runInternal(progressMonitor);
            assertEquals("No WI type defined. Please add a type to the job definition.", status.getMessage());

            jobUnit.setTypes("type1,type2");
            jobUnit.setInspectFields("field1,field2");
            status = jobUnit.runInternal(progressMonitor);

            verify(trackerService, times(1)).queryWorkItems(any(), eq("NOT HAS_VALUE:resolution AND type:(type1 type2)"), eq("id"));

            ArgumentCaptor<RequirementsInspectorService.Context> contextArgument = ArgumentCaptor.forClass(RequirementsInspectorService.Context.class);
            verify(mockedInspectorService.constructed().get(0)).inspectWorkItems(any(), contextArgument.capture());

            assertTrue(contextArgument.getValue().getFields().containsAll(Arrays.asList("field1", "field2")));
            assertEquals("", status.getMessage());
        } finally {
            JobLogger.getInstance().clear();
        }
    }

}
