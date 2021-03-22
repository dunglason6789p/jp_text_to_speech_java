package ntson.controller;

import ntson.model.MyTTSRequest;
import ntson.service.MyTextToSpeechService;
import ntson.util.HTTPUtil;
import ntson.util.JSONUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class MyTTSController {
    
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(MyFileController.class);

    @Autowired
    public void setMyTextToSpeechService(MyTextToSpeechService myTextToSpeechService) {
        this.myTextToSpeechService = myTextToSpeechService;
    }
    private MyTextToSpeechService myTextToSpeechService;
    
    @PostMapping(value = "/tts")
    public void getFile(
            @RequestBody MyTTSRequest request,
            HttpServletResponse httpServletResponse
    ) {
        // Validate request.
        if (request == null || request.getText() == null || request.getText().trim().isEmpty()) {
            HTTPUtil.sendErrorResponse(httpServletResponse,
                    HttpStatus.BAD_REQUEST, "Invalid request! request=" + JSONUtil.jsonString(request));
            return;
        }
        // Process TTS.
        String filePathStr = myTextToSpeechService.processTextToSpeechOrCached(
                request.getText(), request.getLanguage(), request.getGender());
        // Send response.
        HTTPUtil.sendFileResponse(httpServletResponse, filePathStr);
    }
}
