package ch.sbb.polarion.extension.requirements_inspector.service;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspector;
import ch.sbb.polarion.extension.requirements_inspector.util.Consts;
import ch.sbb.polarion.extension.generic.util.JobLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polarion.alm.tracker.model.IWorkItem;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequirementsInspectorServiceTest {

    @Test
    void testNothingHappensWhenNoWorkitems() {
        PolarionService polarionService = mock(PolarionService.class);
        when(polarionService.getFieldData(any(), any())).thenReturn(new ArrayList<>());

        RequirementsInspector executor = mock(RequirementsInspector.class);
        when(executor.inspectWorkitems(any())).thenReturn(generateExecutorResponse());

        RequirementsInspectorService.Context context = new RequirementsInspectorService.Context(true, true);
        assertDoesNotThrow(() -> new RequirementsInspectorService(polarionService, executor).inspectWorkitems(new ArrayList<>(), context));

        //check that none of these methods were called
        verify(polarionService, times(0)).getFieldData(any(), any());
        verify(polarionService, times(0)).updateWorkItemsFields(any(), any());
        verify(executor, times(0)).inspectWorkitems(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInspect() {
        PolarionService polarionService = mock(PolarionService.class);
        when(polarionService.getFieldData(any(), any())).thenReturn(new ArrayList<>());

        RequirementsInspector executor = mock(RequirementsInspector.class);
        when(executor.inspectWorkitems(any())).thenReturn(generateExecutorResponse());

        RequirementsInspectorService.Context context = new RequirementsInspectorService.Context(true, true);
        context.addFieldToInspection("oneMoreField");
        context.addFieldToInspection("oneMoreFieldTwo");
        new RequirementsInspectorService(polarionService, executor).inspectWorkitems(Collections.singletonList(mock(IWorkItem.class)), context);

        ArgumentCaptor<Set<String>> inputItemsCaptor = ArgumentCaptor.forClass(Set.class);
        verify(polarionService).getFieldData(inputItemsCaptor.capture(), any());

        assertEquals(Set.of(Consts.LANGUAGE, Consts.DESCRIPTION, "oneMoreField", "oneMoreFieldTwo"), inputItemsCaptor.getValue());

        ArgumentCaptor<List<Map<String, String>>> dataCaptor = ArgumentCaptor.forClass(List.class);
        verify(polarionService).updateWorkItemsFields(any(), dataCaptor.capture());

        assertEquals("someLang", dataCaptor.getValue().get(0).get(Consts.LANGUAGE));
        assertEquals("someTitle", dataCaptor.getValue().get(0).get(Consts.TITLE));
        assertEquals("someDesc", dataCaptor.getValue().get(0).get(Consts.DESCRIPTION));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInspectSkipLanguage() {
        PolarionService polarionService = mock(PolarionService.class);
        when(polarionService.getFieldData(any(), any())).thenReturn(new ArrayList<>());

        RequirementsInspector executor = mock(RequirementsInspector.class);
        when(executor.inspectWorkitems(any())).thenReturn(generateExecutorResponse());

        RequirementsInspectorService.Context context = new RequirementsInspectorService.Context(false, false);
        new RequirementsInspectorService(polarionService, executor).inspectWorkitems(Collections.singletonList(mock(IWorkItem.class)), context);

        ArgumentCaptor<Set<String>> inputItemsCaptor = ArgumentCaptor.forClass(Set.class);
        verify(polarionService).getFieldData(inputItemsCaptor.capture(), any());

        assertEquals(Set.of(Consts.TITLE, Consts.LANGUAGE, Consts.DESCRIPTION), inputItemsCaptor.getValue());

        ArgumentCaptor<List<Map<String, String>>> dataCaptor = ArgumentCaptor.forClass(List.class);
        verify(polarionService).updateWorkItemsFields(any(), dataCaptor.capture());

        assertFalse(dataCaptor.getValue().get(0).containsKey(Consts.LANGUAGE));
        assertEquals("someTitle", dataCaptor.getValue().get(0).get(Consts.TITLE));
        assertEquals("someDesc", dataCaptor.getValue().get(0).get(Consts.DESCRIPTION));
    }

    @Test
    void testLogMessage() {
        HashMap<String, String> workItemResult = new HashMap<>();
        List<Map<String, String>> data = new ArrayList<>();
        workItemResult.put("smellComplex", "0");
        workItemResult.put("smellPassive", "1");
        workItemResult.put("smellWeakword", "2");
        workItemResult.put("smellComparative", "3");
        workItemResult.put("missingProcessword", "4");
        workItemResult.put("smellDescription", "ABCTEST");
        data.add(workItemResult);
        RequirementsInspectorService.logResults(data);

        String output = JobLogger.getInstance().getLog();
        assertTrue(output.contains("Total smellComplex 0"));
        assertTrue(output.contains("Total smellPassive 1"));
        assertTrue(output.contains("Total smellWeakword 1"));
        assertTrue(output.contains("Total smellComparative 1"));
        assertTrue(output.contains("Total missingProcessword 1"));
        assertTrue(output.contains("ABCTEST"));
    }

    @SneakyThrows
    private String generateExecutorResponse() {
        return new ObjectMapper().writeValueAsString(
                List.of(Map.of(
                        Consts.TITLE, "someTitle",
                        Consts.DESCRIPTION, "someDesc",
                        Consts.LANGUAGE, "someLang"
                )));
    }
}
