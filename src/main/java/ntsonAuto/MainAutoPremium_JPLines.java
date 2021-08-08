package ntsonAuto;

import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import ntson.enums.LanguageCode;
import ntson.service.MyTextToSpeechService;
import ntson.service.MyTextToSpeechServicePremium;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static ntson.util.FileUtil.readEntireTextFile;

public class MainAutoPremium_JPLines {
    private static final Logger logger = LoggerFactory.getLogger(MainAutoPremium_JPLines.class);
    private static final MyTextToSpeechService myTextToSpeechService = new MyTextToSpeechService();
    private static MyTextToSpeechServicePremium myTextToSpeechServicePremium = null;
    public static void main(String[] args) throws Exception {
        MainAutoPremium_JPLines.myTextToSpeechServicePremium = new MyTextToSpeechServicePremium(
                MainAutoPremium_JPLines.myTextToSpeechService.getTextToSpeechClient());
        final String textEntire = readEntireTextFile(new File("data/jpLines.txt"), StandardCharsets.UTF_8);
        final String[] lines = textEntire.split("\\r?\\n");
        long charCount = 0;
        for (final String jpLine : lines) {
            try {
                logger.info("Sleeping...");
                Thread.sleep(500);
                String preProcessedText = myTextToSpeechServicePremium.preprocessJapaneseText(jpLine);
                myTextToSpeechServicePremium.processTextToSpeechOrCachedWaveNetJp(
                        preProcessedText,
                        //AudioEncoding.LINEAR16
                        AudioEncoding.OGG_OPUS
                );
                charCount += preProcessedText.length();
                logger.info("charCount={}", charCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
