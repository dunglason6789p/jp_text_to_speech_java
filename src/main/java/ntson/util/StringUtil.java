package ntson.util;

public class StringUtil {
    public static boolean isNullOrBlank(String string) {
        if (string == null) {
            return true;
        }
        return string.trim().isEmpty();
    }
}
