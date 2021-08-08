package ntsonAuto;

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

public class MainAutoPremium {
    private static Logger logger = LoggerFactory.getLogger(MainAutoPremium.class);
    private static final MyTextToSpeechService myTextToSpeechService = new MyTextToSpeechService();
    private static MyTextToSpeechServicePremium myTextToSpeechServicePremium = null;
    public static void main(String[] args) throws Exception {
        MainAutoPremium.myTextToSpeechServicePremium = new MyTextToSpeechServicePremium(
                MainAutoPremium.myTextToSpeechService.getTextToSpeechClient());
        final Gson gson = new Gson();
        final String lessonsJsonStr = readEntireTextFile(new File("data/lessons.json"), StandardCharsets.UTF_8);
        long charCount = 0;
        Map<?,?> jsonMap = gson.fromJson(lessonsJsonStr, Map.class);
        for (Object key : jsonMap.keySet()) {
            logger.info("LESSON "+key+" ===========================================================================");
            Object value = jsonMap.get(key);
            try {
                ArrayList<?> arrayList = (ArrayList)value;
                for (Object obj : arrayList) {
                    logger.info("Sleeping...");
                    Thread.sleep(300);
                    if (obj instanceof LinkedTreeMap) {
                        LinkedTreeMap<?,?> linkedTreeMap = (LinkedTreeMap)obj;
                        String kanji = (String)linkedTreeMap.get("kanji");
                        String hiragana = (String)linkedTreeMap.get("hiragana");
                        String nom = (String)linkedTreeMap.get("nom");
                        String meaning = (String)linkedTreeMap.get("meaning");
                        if (kanji != null) {
                            myTextToSpeechServicePremium.processTextToSpeechOrCachedWaveNetJp(kanji);
                            charCount += kanji.length();
                        }
                        if (meaning != null) {
                            myTextToSpeechService.processTextToSpeechOrCachedV2(meaning, LanguageCode.VI_VN, true);
                            charCount += meaning.length();
                        }
                        logger.info("charCount={}", charCount);
                    } else {
                        System.err.println(obj.getClass());
                        System.err.println(obj);
                    }
                }
            } catch (ClassCastException classCastException) {
                classCastException.printStackTrace();
            }
        }
    }
}
