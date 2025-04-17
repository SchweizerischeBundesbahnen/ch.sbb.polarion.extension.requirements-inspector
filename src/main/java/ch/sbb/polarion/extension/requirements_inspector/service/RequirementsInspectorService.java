package ch.sbb.polarion.extension.requirements_inspector.service;

import ch.sbb.polarion.extension.generic.util.JobLogger;
import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspector;
import ch.sbb.polarion.extension.requirements_inspector.util.Consts;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.HashMap;
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

    @VisibleForTesting
    public static void logResults(List<Map<String, String>> data) {
        JobLogger jobLogger = JobLogger.getInstance();
        jobLogger.log("%sREQUIREMENTS INSPECTOR RESULTS", System.lineSeparator());
        jobLogger.separator();
        jobLogger.log("INDIVIDUAL RESULTS");
        jobLogger.separator();
        HashMap<String, Integer> numIssues = data.stream().reduce(new HashMap<>(), (subtotal, element) -> {
            subtotal.put("numComplex", subtotal.getOrDefault("numComplex", 0) + (element.getOrDefault("smellComplex", "0").equals("0") ? 0 : 1));
            subtotal.put("numPassive", subtotal.getOrDefault("numPassive", 0) + (element.getOrDefault("smellPassive", "0").equals("0") ? 0 : 1));
            subtotal.put("numWeakword", subtotal.getOrDefault("numWeakword", 0) + (element.getOrDefault("smellWeakword", "0").equals("0") ? 0 : 1));
            subtotal.put("numComparative", subtotal.getOrDefault("numComparative", 0) + (element.getOrDefault("smellComparative", "0").equals("0") ? 0 : 1));
            subtotal.put("numMissingProcessword", subtotal.getOrDefault("numMissingProcessword", 0) + (element.getOrDefault("missingProcessword", "false").equalsIgnoreCase("false") ? 0 : 1));
            if (!element.getOrDefault("smellDescription", "").isEmpty()) {
                jobLogger.log("Workitem with ID %s has smellDescription %s", element.get("id"), element.get("smellDescription"));
            }
            return subtotal;
        }, (m, m2) -> {
            m.putAll(m2);
            return m;
        });
        jobLogger.separator();
        jobLogger.log("TOTALS");
        jobLogger.separator();
        jobLogger.log("Total smellComplex %d", numIssues.get("numComplex"));
        jobLogger.log("Total smellPassive %d", numIssues.get("numPassive"));
        jobLogger.log("Total smellWeakword %d", numIssues.get("numWeakword"));
        jobLogger.log("Total smellComparative %d", numIssues.get("numComparative"));
        jobLogger.log("Total missingProcessword %d", numIssues.get("numMissingProcessword"));
    }

    public static Context getContext(boolean ignoreInspectTitle, boolean addMissingLanguage, @NotNull List<String> addFields) {
        RequirementsInspectorService.Context context =
                new RequirementsInspectorService.Context(
                        ignoreInspectTitle, addMissingLanguage);
        addFields.forEach(context::addFieldToInspection);
        return context;
    }

    /**
     * Inspects work items by executing a Python script. Populates
     * each work item with the inspection results.
     *
     * @param workItems the list of work items to inspect
     * @param context   context data
     */
    public List<Map<String, String>> inspectWorkItems(List<IWorkItem> workItems, Context context) {

        if (workItems.isEmpty()) {
            LOGGER.info("There are no Workitems to inspect");
            JobLogger.getInstance().log("There are no Workitems to inspect");
            return List.of();
        }

        List<Map<String, String>> inputData = polarionService.getFieldData(context.fields, workItems);
        List<Map<String, String>> data = requirementsInspector.inspectWorkitems(inputData);

        if (!context.addMissingLanguage) {
            data.forEach(item -> item.remove(Consts.LANGUAGE));
        }

        LOGGER.info("Update Workitems with JSON Data");
        polarionService.updateWorkItemsFields(workItems, data);
        logResults(data);
        return data;
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
