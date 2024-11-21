package ch.sbb.polarion.extension.requirements_inspector;

import ch.sbb.polarion.extension.requirements_inspector.service.PolarionService;
import com.polarion.platform.jobs.IJobDescriptor;
import com.polarion.platform.jobs.IJobUnit;
import com.polarion.platform.jobs.IJobUnitFactory;
import com.polarion.platform.jobs.spi.BasicJobDescriptor;
import com.polarion.platform.jobs.spi.JobParameterPrimitiveType;
import com.polarion.platform.jobs.spi.SimpleJobParameter;

public class RequirementsInspectorJobUnitFactory implements IJobUnitFactory {

    private final PolarionService polarionService = new PolarionService();

    public IJobUnit createJobUnit(String name) {
        return new RequirementsInspectorJobUnit(name, this, polarionService);
    }

    public IJobDescriptor getJobDescriptor(IJobUnit jobUnit) {
        BasicJobDescriptor desc = new BasicJobDescriptor("Inspect WI description based on defined inspection criteria", jobUnit);

        JobParameterPrimitiveType stringType = new JobParameterPrimitiveType("String", String.class);
        JobParameterPrimitiveType boolType = new JobParameterPrimitiveType("Boolean", Boolean.class);

        desc.addParameter(new SimpleJobParameter(desc.getRootParameterGroup(), "types",
                "String of comma seperated types which should be inspected", stringType));
        desc.addParameter(new SimpleJobParameter(desc.getRootParameterGroup(), "addMissingLanguage",
                "Should language that is missing, be set to language that has been detected", boolType));
        desc.addParameter(new SimpleJobParameter(desc.getRootParameterGroup(), "inspectTitle",
                "Should the title of the work items be inspected for problems", boolType));
        desc.addParameter(new SimpleJobParameter(desc.getRootParameterGroup(), "inspectFields",
                "String of a comma seperated field ids that should also be inspected", stringType));
        desc.addParameter(new SimpleJobParameter(desc.getRootParameterGroup(), "filter",
                "String of a filter query that is used to filter the workitems", stringType));

        return desc;
    }

    public String getName() {
        return IRequirementsInspectorJobUnit.JOB_NAME;
    }
}
