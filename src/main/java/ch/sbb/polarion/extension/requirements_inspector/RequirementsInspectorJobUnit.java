package ch.sbb.polarion.extension.requirements_inspector;

import ch.sbb.polarion.extension.requirements_inspector.requirements_inspector.RequirementsInspectorServiceConnector;
import ch.sbb.polarion.extension.requirements_inspector.service.RequirementsInspectorService;
import ch.sbb.polarion.extension.requirements_inspector.service.PolarionService;
import ch.sbb.polarion.extension.requirements_inspector.util.Consts;
import ch.sbb.polarion.extension.requirements_inspector.util.StringUtil;
import com.polarion.alm.projects.model.IProject;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.StringUtils;
import com.polarion.core.util.logging.Logger;
import com.polarion.platform.context.IContext;
import com.polarion.platform.jobs.IJobStatus;
import com.polarion.platform.jobs.IJobUnitFactory;
import com.polarion.platform.jobs.IProgressMonitor;
import com.polarion.platform.jobs.spi.AbstractJobUnit;
import lombok.Setter;
import org.jetbrains.annotations.VisibleForTesting;
import ch.sbb.polarion.extension.generic.util.JobLogger;

import java.util.List;

@Setter
public class RequirementsInspectorJobUnit extends AbstractJobUnit implements IRequirementsInspectorJobUnit {

    private static final Logger logger = Logger.getLogger(RequirementsInspectorJobUnit.class);

    private final PolarionService polarionService;
    private final RequirementsInspectorService requirementsInspectorService;
    private String types;
    private Boolean addMissingLanguage = false;
    private Boolean inspectTitle = true;
    private String inspectFields = "";
    private String filter = "";

    public RequirementsInspectorJobUnit(String name, IJobUnitFactory creator, PolarionService polarionService) {
        super(name, creator);
        this.polarionService = polarionService;
        this.requirementsInspectorService = new RequirementsInspectorService(polarionService, new RequirementsInspectorServiceConnector());
    }

    @VisibleForTesting
    @SuppressWarnings("unchecked")
    public IJobStatus runInternal(IProgressMonitor progress) {
        IContext scope = getScope();

        progress.beginTask(getName(), 0);
        try {

            // Throw Exception if no type is defined
            if (StringUtils.isEmptyTrimmed(types)) {
                throw new IllegalStateException("No WI type defined. Please add a type to the job definition.");
            }

            String query = buildQuery();
            logger.info("query: " + query);

            // get work items
            IProject project = polarionService.getProjectService().getProjectForContextId(scope.getId());
            List<IWorkItem> workItems = polarionService.getTrackerService().queryWorkItems(project, query, "id");

            RequirementsInspectorService.Context context = new RequirementsInspectorService.Context(!inspectTitle, addMissingLanguage);
            // add given field ids to the list of fields to inspect in the RequirementsInspector class
            for (String fieldId : StringUtil.stringToList(inspectFields, Consts.SEPARATOR).stream().filter(str -> !str.isEmpty()).toList()) {
                context.addFieldToInspection(fieldId);
            }
            requirementsInspectorService.inspectWorkItems(workItems, context);

            return getStatusOK(JobLogger.getInstance().getLog());
        } catch (Exception e) {
            return getStatusFailed(e.getMessage(), e);
        } finally {
            progress.done();
        }
    }

    /**
     * Builds a query string based on defined work item types and an optional filter.
     */
    private String buildQuery() {
        // Get all WIs of the defined types in the project
        String query = "NOT HAS_VALUE:resolution AND type:(" + StringUtil.removeSeparatorChars(this.types, Consts.SEPARATOR) + ")";

        // add filter if defined
        if (!this.filter.isBlank()) {
            query = query.concat(" AND " + filter);
        }

        // Return the complete query string
        return query;
    }
}
