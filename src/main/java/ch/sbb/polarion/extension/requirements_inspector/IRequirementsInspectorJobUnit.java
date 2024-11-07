package ch.sbb.polarion.extension.requirements_inspector;

import com.polarion.platform.jobs.IJobUnit;

@SuppressWarnings("unused")
public interface IRequirementsInspectorJobUnit extends IJobUnit {

    String JOB_NAME = "requirementsInspection";

    void setTypes(String types);

    void setAddMissingLanguage(Boolean addMissingLanguage);

    void setInspectTitle(Boolean inspectTitle);

    void setInspectFields(String inspectFields);

    void setFilter(String filter);

}
