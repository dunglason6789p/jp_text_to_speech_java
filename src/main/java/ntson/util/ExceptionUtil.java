package ntson.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ExceptionUtil {
    public static String getStackTraceAsSingleString(Exception exception) {
        return Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("|"));
    }
}
