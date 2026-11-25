package ch.sbb.polarion.extension.requirements_inspector;

import ch.sbb.polarion.extension.generic.util.JobLogger;
import ch.sbb.polarion.extension.requirements_inspector.service.RequirementsInspectorPolarionService;
import ch.sbb.polarion.extension.requirements_inspector.service.RequirementsInspectorService;
import com.polarion.alm.projects.IProjectService;
import com.polarion.alm.projects.model.IProject;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IWorkItem;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequirementsInspectorJobUnitTest {

    @Test
    void jobRunTest() {
        JobLogger.getInstance().clear();

        RequirementsInspectorPolarionService polarionService = mock(RequirementsInspectorPolarionService.class);

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

    @Test
    @SuppressWarnings("unchecked")
    void jobParametersReachTheQueryAndTheInspection() {
        JobLogger.getInstance().clear();

        RequirementsInspectorPolarionService polarionService = mock(RequirementsInspectorPolarionService.class);
        IProjectService projectService = mock(IProjectService.class);
        when(polarionService.getProjectService()).thenReturn(projectService);
        IProject project = mock(IProject.class);
        when(projectService.getProjectForContextId(any())).thenReturn(project);
        ITrackerService trackerService = mock(ITrackerService.class);
        when(polarionService.getTrackerService()).thenReturn(trackerService);
        IPObjectList<IWorkItem> workItems = mock(IPObjectList.class);
        when(trackerService.queryWorkItems(any(), any(), any())).thenReturn(workItems);

        List<List<?>> contextArguments = new ArrayList<>();
        try (MockedConstruction<RequirementsInspectorService> ignored = Mockito.mockConstruction(RequirementsInspectorService.class);
             MockedConstruction<RequirementsInspectorService.Context> contexts = Mockito.mockConstruction(RequirementsInspectorService.Context.class,
                     (mock, context) -> contextArguments.add(context.arguments()))) {

            RequirementsInspectorJobUnit jobUnit = new RequirementsInspectorJobUnit("testJobName", mock(IJobUnitFactory.class), polarionService);
            jobUnit.setJob(mock(IJob.class));
            IContext scope = mock(IContext.class);
            IContextId contextId = mock(IContextId.class);
            when(scope.getId()).thenReturn(contextId);
            jobUnit.setScope(scope);

            jobUnit.setTypes("type1");
            jobUnit.setFilter("status:draft");
            jobUnit.setInspectTitle(true);
            jobUnit.setAddMissingLanguage(true);
            jobUnit.runInternal(mock(IProgressMonitor.class));

            verify(trackerService).queryWorkItems(any(), eq("NOT HAS_VALUE:resolution AND type:(type1) AND status:draft"), eq("id"));
            // the context takes 'ignoreInspectTitle', the negation of the job's 'inspectTitle', then 'addMissingLanguage'
            assertEquals(1, contexts.constructed().size());
            assertEquals(List.of(false, true), contextArguments.get(0));
        } finally {
            JobLogger.getInstance().clear();
        }
    }

}
