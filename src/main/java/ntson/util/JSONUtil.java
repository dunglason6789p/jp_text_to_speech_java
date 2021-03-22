package ntson.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String jsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }
}
