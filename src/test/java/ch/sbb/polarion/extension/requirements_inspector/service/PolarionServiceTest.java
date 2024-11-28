package ch.sbb.polarion.extension.requirements_inspector.service;

import ch.sbb.polarion.extension.requirements_inspector.util.Consts;
import com.polarion.alm.projects.IProjectService;
import com.polarion.alm.shared.api.transaction.RunnableInWriteTransaction;
import com.polarion.alm.shared.api.transaction.TransactionalExecutor;
import com.polarion.alm.shared.api.transaction.WriteTransaction;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.types.Text;
import com.polarion.platform.IPlatformService;
import com.polarion.platform.persistence.model.IPrototype;
import com.polarion.platform.persistence.spi.EnumOption;
import com.polarion.platform.security.ISecurityService;
import com.polarion.platform.service.repository.IRepositoryService;
import com.polarion.subterra.base.data.model.ICustomField;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PolarionServiceTest {

    private static final String TEXT_FIELD_ID = "textFieldId";
    private static final String ENUM_FIELD_ID = "enumFieldId";
    private static final String INT_FIELD_ID = "intFieldId";
    private static final String NULL_FIELD_ID = "nullFieldId";

    @Test
    void testGetFieldData() {

        PolarionService polarionService = new PolarionService(mock(ITrackerService.class), mock(IProjectService.class), mock(ISecurityService.class), mock(IPlatformService.class), mock(IRepositoryService.class));

        IWorkItem workItem1 = mock(IWorkItem.class);
        when(workItem1.getId()).thenReturn("id1");
        when(workItem1.getTitle()).thenReturn("title1");
        when(workItem1.getDescription()).thenReturn(Text.plain("description1"));
        when(workItem1.getCustomField(TEXT_FIELD_ID)).thenReturn(Text.plain("text1"));
        when(workItem1.getCustomField(ENUM_FIELD_ID)).thenReturn(new EnumOption("enumId", "optionId1"));
        when(workItem1.getCustomField(INT_FIELD_ID)).thenReturn(1);
        when(workItem1.getCustomField(NULL_FIELD_ID)).thenReturn(null);

        IWorkItem workItem2 = mock(IWorkItem.class);
        when(workItem2.getId()).thenReturn("id2");
        when(workItem2.getTitle()).thenReturn("title2");
        when(workItem2.getDescription()).thenReturn(Text.plain("description2"));
        when(workItem2.getCustomField(TEXT_FIELD_ID)).thenReturn(Text.plain("text2"));
        when(workItem2.getCustomField(ENUM_FIELD_ID)).thenReturn(new EnumOption("enumId", "optionId2"));
        when(workItem2.getCustomField(INT_FIELD_ID)).thenReturn(2);
        when(workItem2.getCustomField(NULL_FIELD_ID)).thenReturn(null);

        List<Map<String, String>> resultList = polarionService.getFieldData(Set.of(Consts.TITLE, Consts.DESCRIPTION, TEXT_FIELD_ID, ENUM_FIELD_ID, INT_FIELD_ID, NULL_FIELD_ID), Arrays.asList(workItem1, workItem2));
        assertEquals(2, resultList.size());

        resultList.forEach(item -> {
            switch (item.get(Consts.ID)) {
                case "id1" -> {
                    assertEquals("title1", item.get(Consts.TITLE));
                    assertEquals("description1", item.get(Consts.DESCRIPTION));
                    assertEquals("text1", item.get(TEXT_FIELD_ID));
                    assertEquals("optionId1", item.get(ENUM_FIELD_ID));
                    assertEquals("", item.get(INT_FIELD_ID));
                    assertEquals("", item.get(NULL_FIELD_ID));
                }
                case "id2" -> {
                    assertEquals("title2", item.get(Consts.TITLE));
                    assertEquals("description2", item.get(Consts.DESCRIPTION));
                    assertEquals("text2", item.get(TEXT_FIELD_ID));
                    assertEquals("optionId2", item.get(ENUM_FIELD_ID));
                    assertEquals("", item.get(INT_FIELD_ID));
                    assertEquals("", item.get(NULL_FIELD_ID));
                }
                default -> throw new AssertionError("unknown item");
            }
        });
    }

    @Test
    @SuppressWarnings("rawtypes")
    void testUpdateWorkItemsFields() {
        PolarionService polarionService = new PolarionService(mock(ITrackerService.class), mock(IProjectService.class), mock(ISecurityService.class), mock(IPlatformService.class), mock(IRepositoryService.class));

        IWorkItem workItem1 = mock(IWorkItem.class);
        when(workItem1.getId()).thenReturn("id1");
        mockCustomFieldRelatedData(workItem1);

        IWorkItem workItem2 = mock(IWorkItem.class);
        when(workItem2.getId()).thenReturn("id2");
        mockCustomFieldRelatedData(workItem2);

        try (final MockedStatic<TransactionalExecutor> executor = mockStatic(TransactionalExecutor.class)) {
            executor.when(() -> TransactionalExecutor.executeInWriteTransaction(any())).thenAnswer(invocation -> {
                RunnableInWriteTransaction runnable = invocation.getArgument(0);
                return runnable.run(mock(WriteTransaction.class));
            });

            polarionService.updateWorkItemsFields(Arrays.asList(workItem1, workItem2), Arrays.asList(
                    Map.of(Consts.ID, "id1", "resultFieldId", "resultValue1"),
                    Map.of(Consts.ID, "id2", "resultFieldId", "resultValue2")
            ));

            verify(workItem1, times(1)).setCustomField(eq("resultFieldId"), any());
            verify(workItem2, times(1)).setCustomField(eq("resultFieldId"), any());
        }
    }

    private void mockCustomFieldRelatedData(IWorkItem workItem) {
        IPrototype prototype = mock(IPrototype.class);
        when(prototype.isKeyDefined(any())).thenReturn(false);
        when(workItem.getPrototype()).thenReturn(prototype);
        ICustomField customField = mock(ICustomField.class);
        when(workItem.getCustomFieldPrototype(any())).thenReturn(customField);
    }
}
