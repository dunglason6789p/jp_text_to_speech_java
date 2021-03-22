package ntson.util;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HTTPUtil {
    public static void sendErrorResponse(
            HttpServletResponse httpServletResponse,
            HttpStatus httpStatusCode,
            String errorMessage
    ) {
        try {
            httpServletResponse.sendError(httpStatusCode.value(), errorMessage);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    public static void sendFileResponse(
            HttpServletResponse httpServletResponse,
            String filePathStr
    ) {
        try {
            File file = new File(filePathStr);
            InputStream inputStream = new FileInputStream(file);
            // copy InputStream to response's OutputStream:
            IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
            httpServletResponse.flushBuffer();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            String stackTrackStr = ExceptionUtil.getStackTraceAsSingleString(ioException);
            sendErrorResponse(httpServletResponse, HttpStatus.INTERNAL_SERVER_ERROR,
                    "StackTrace:" + stackTrackStr);
        }
    }
}
