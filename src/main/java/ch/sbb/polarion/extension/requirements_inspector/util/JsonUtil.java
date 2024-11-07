package ch.sbb.polarion.extension.requirements_inspector.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class JsonUtil {

    @SneakyThrows
    public String writeInputJson(List<Map<String, String>> inputData) {
        return new ObjectMapper().writeValueAsString(inputData);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public List<Map<String, String>> parseOutputJson(String json) {
        try (StringReader reader = new StringReader(json)) {
            return ((JSONArray) new JSONParser().parse(reader)).stream()
                    .map(jsonObject -> {
                        HashMap<String, String> itemData = new HashMap<>();
                        ((JSONObject) jsonObject)
                                .forEach((key, value) -> itemData.put(String.valueOf(key), String.valueOf(value)));
                        return itemData;
                    }).toList();
        }
    }
}
