package ntson.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    public static final Logger logger = LoggerFactory.getLogger(LogUtil.class);
    public static <T> T logExceptionAndReturnNull(Exception e) {
        logger.error("", e);
        return null;
    }
    public static <T> T ignoreExceptionAndReturnNull(Exception e) {
        logger.error("", e);
        return null;
    }
}
