package ch.sbb.polarion.extension.requirements_inspector;

import ch.sbb.polarion.extension.generic.GenericUiServlet;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequirementsInspectorAppServletTest {

    /**
     * The webapp name has to match the context registered in plugin.xml and the paths hivemodule.xml
     * opens; a mismatch serves nothing and stays invisible until the administration page is opened.
     */
    @Test
    void servesTheReactAppWebapp() throws Exception {
        RequirementsInspectorAppServlet servlet = new RequirementsInspectorAppServlet();

        Field field = GenericUiServlet.class.getDeclaredField("webAppName");
        field.setAccessible(true);

        assertEquals("requirements-inspector-app", field.get(servlet));
    }
}
