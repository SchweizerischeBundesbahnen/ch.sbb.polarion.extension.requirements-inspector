package ch.sbb.polarion.extension.requirements_inspector.service;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspector;
import ch.sbb.polarion.extension.requirements_inspector.util.Consts;
import ch.sbb.polarion.extension.requirements_inspector.util.JsonUtil;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.logging.Logger;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Performs inspection of work items using the python_requirements_inspector python module.
 */
public class RequirementsInspectorService {

    private static final Logger LOGGER = Logger.getLogger(RequirementsInspectorService.class);

    private final PolarionService polarionService;
    private final RequirementsInspector requirementsInspector;

    public RequirementsInspectorService(PolarionService polarionService, RequirementsInspector requirementsInspector) {
        this.polarionService = polarionService;
        this.requirementsInspector = requirementsInspector;
    }

    /**
     * Inspects work items by executing a Python script. Populates
     * each work item with the inspection results.
     *
     * @param workItems the list of work items to inspect
     * @param context   context data
     */
    public void inspectWorkitem(List<IWorkItem> workItems, Context context) {

        if (workItems.isEmpty()) {
            LOGGER.info("There is no Workitems to inspect");
            return;
        }

        List<Map<String, String>> inputData = polarionService.getFieldData(context.fields, workItems);
        String output = requirementsInspector.inspectWorkitems(inputData);
        List<Map<String, String>> data = JsonUtil.parseOutputJson(output);

        if (!context.addMissingLanguage) {
            data.forEach(item -> item.remove(Consts.LANGUAGE));
        }

        LOGGER.info("Update Workitems with JSON Data");
        polarionService.updateWorkItemsFields(workItems, data);
    }

    public static class Context {

        private final Set<String> fields = Stream.of(Consts.TITLE, Consts.DESCRIPTION, Consts.LANGUAGE).collect(Collectors.toSet());
        private final boolean addMissingLanguage;

        public Context(boolean ignoreInspectTitle, boolean addMissingLanguage) {
            this.addMissingLanguage = addMissingLanguage;
            if (ignoreInspectTitle) {
                fields.remove(Consts.TITLE);
            }
        }

        public void addFieldToInspection(String fieldId) {
            fields.add(fieldId);
        }

        @VisibleForTesting
        public Set<String> getFields() {
            return fields;
        }
    }
}
