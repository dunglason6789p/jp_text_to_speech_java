package ntson.util;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ExceptionUtil {
    public static String getStackTraceAsSingleString(Exception exception) {
        return Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("|"));
    }
    public static <T> T tryGet(ThrowableSupplier<T, Exception> supplier, Function<Exception, T> exceptionHandler) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return exceptionHandler.apply(e);
        }
    }
    @FunctionalInterface
    public static interface ThrowableSupplier<T, E extends Exception> {
        public T get() throws E;
    }
}
