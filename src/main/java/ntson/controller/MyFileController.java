package ntson.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class MyFileController {
    
    @SuppressWarnings("unused")
    Logger logger = LoggerFactory.getLogger(MyFileController.class);
    
    @RequestMapping(value = "/test-file/{file_name}", method = RequestMethod.GET)
    public void getFile(
            @PathVariable("file_name") String fileName,
            HttpServletResponse response
    ) {
        String filePathStr = "file/test/" + fileName;
        try {
            File file = new File(filePathStr);
            InputStream inputStream = new FileInputStream(file);
            // copy InputStream to response's OutputStream:
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            ex.printStackTrace();
            if (ex instanceof FileNotFoundException) {
                try {
                    String errorMessageStr = String.format("File %s not found! Path=%s", fileName, filePathStr);
                    logger.error(errorMessageStr);
                    response.sendError(500, errorMessageStr);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
