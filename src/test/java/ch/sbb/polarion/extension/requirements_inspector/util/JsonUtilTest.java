package ch.sbb.polarion.extension.requirements_inspector.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonUtilTest {

    @Test
    void testWrite() {
        List<Map<String, String>> inputList = List.of(Map.of("fieldId", "fieldValue"));
        assertEquals("[{\"fieldId\":\"fieldValue\"}]", JsonUtil.writeInputJson(inputList));
    }

    @Test
    void testParse() {
        List<Map<String, String>> list = JsonUtil.parseOutputJson("[{\"id\":\"1\",\"title\":\"title1\"},{\"id\":\"2\",\"title\":\"title2\"}]");
        assertEquals(2, list.size());
        assertEquals(Set.of("1", "2"), list.stream().map(m -> m.get("id")).collect(Collectors.toSet()));
        assertEquals(Set.of("title1", "title2"), list.stream().map(m -> m.get("title")).collect(Collectors.toSet()));
    }

    @Test
    void testResultMapMutable() {
        Map<String, String> resultMap = JsonUtil.parseOutputJson("[{\"id\":\"1\",\"title\":\"title1\"}]").get(0);
        assertEquals(2, resultMap.size());
        resultMap.remove("id");
        assertEquals(1, resultMap.size());

    }

}
