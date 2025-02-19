package ch.sbb.polarion.extension.requirements_inspector.service;

import ch.sbb.polarion.extension.requirements_inspector.util.Consts;
import com.polarion.alm.projects.IProjectService;
import com.polarion.alm.shared.api.transaction.TransactionalExecutor;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.ITrackerProject;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.logging.Logger;
import com.polarion.core.util.types.Text;
import com.polarion.platform.IPlatformService;
import com.polarion.platform.persistence.spi.EnumOption;
import com.polarion.platform.security.ISecurityService;
import com.polarion.platform.service.repository.IRepositoryService;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("squid:S2176") // class named same as parent intentionally
public class PolarionService extends ch.sbb.polarion.extension.generic.service.PolarionService {

  private static final Logger LOGGER = Logger.getLogger(PolarionService.class);

  public PolarionService() {}

  public PolarionService(
      @NotNull ITrackerService trackerService,
      @NotNull IProjectService projectService,
      @NotNull ISecurityService securityService,
      @NotNull IPlatformService platformService,
      @NotNull IRepositoryService repositoryService) {
    super(trackerService, projectService, securityService, platformService, repositoryService);
  }

  @SneakyThrows
  public List<Map<String, String>> getFieldData(Set<String> fields, List<IWorkItem> workItems) {
    return workItems.stream()
        .map(
            workItem -> {
              Map<String, String> map = new HashMap<>();
              map.put(Consts.ID, workItem.getId());
              fields.forEach(fieldId -> map.put(fieldId, getWorkItemFieldData(workItem, fieldId)));
              return map;
            })
        .toList();
  }

  @SneakyThrows
  public void updateWorkItemsFields(List<IWorkItem> workItems, List<Map<String, String>> dataMap) {
    for (IWorkItem workItem : workItems) {
      String workItemId = workItem.getId();
      dataMap.stream()
          .filter(map -> Objects.equals(workItemId, map.get(Consts.ID)))
          .findFirst()
          .ifPresent(
              fields ->
                  fields.forEach(
                      (key, value) -> {
                        if (!Objects.equals(Consts.ID, key)) { // do not process ID field
                          LOGGER.info(
                              "update workItem: %s custom field '%s' with value: '%s'"
                                  .formatted(workItemId, key, value));
                          setFieldValue(workItem, key, value);
                        }
                      }));
    }

    TransactionalExecutor.executeInWriteTransaction(
        writeTransaction -> {
          workItems.forEach(IWorkItem::save);
          return true;
        });
  }

  public @NotNull IWorkItem getWorkItem(@NotNull String projectId, @NotNull String workItemId) {
    ITrackerProject trackerProject = this.getTrackerProject(projectId);
    return trackerProject.getWorkItem(workItemId);
  }

  public List<IWorkItem> getWorkItems(String projectId, List<String> workItemIds) {
    ITrackerProject trackerProject = this.getTrackerProject(projectId);
    return workItemIds.stream().map(trackerProject::getWorkItem).toList();
  }

  private String getWorkItemFieldData(IWorkItem workItem, String workItemFieldId) {
    return switch (workItemFieldId) {
      case Consts.ID -> workItem.getId();
      case Consts.TITLE -> workItem.getTitle();
      case Consts.DESCRIPTION -> getTextAsString(workItem.getDescription());
      default -> getWorkItemCustomFieldContent(workItem, workItemFieldId);
    };
  }

  private String getWorkItemCustomFieldContent(IWorkItem workItem, String customFieldId) {
    Object customField = workItem.getCustomField(customFieldId);
    String fieldContent = "";

    if (customField instanceof String string) {
      fieldContent = string;
    } else if (customField instanceof Text text) {
      fieldContent = getTextAsString(text);
    } else if (customField instanceof EnumOption option) {
      fieldContent = option.getId();
    }

    return fieldContent;
  }

  private String getTextAsString(Text fieldContent) {
    return fieldContent == null ? "" : fieldContent.convertToPlainText().getContent();
  }
}
