package ntsonAuto;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import ntson.service.MyTextToSpeechService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class MainAuto {
    private static final MyTextToSpeechService myTextToSpeechService = new MyTextToSpeechService();
    public static void mainOLD(String[] args) {
        AtomicInteger lineCount = new AtomicInteger(0);
        try (Stream<String> lines = Files.lines(Paths.get("data/lessons.json"), StandardCharsets.UTF_8)) {
            lines.forEach(line -> {
                System.out.println(line);
                lineCount.incrementAndGet();
            });
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        System.out.println("Total line count: " + lineCount.get());
    }
    public static void main(String[] args) {
        Gson gson = new Gson();
        try (Stream<String> lines = Files.lines(Paths.get("data/lessons.json"), StandardCharsets.UTF_8)) {
            String firstLine = lines.findFirst().orElse(null);
            if (firstLine != null) {
                Map<?,?> jsonMap = gson.fromJson(firstLine, Map.class);
                for (Object key : jsonMap.keySet()) {
                    System.out.println("LESSON "+key+" ===========================================================================");
                    Object value = jsonMap.get(key);
                    try {
                        //System.out.println(value);
                        ArrayList<?> arrayList = (ArrayList)value;
                        for (Object obj : arrayList) {
                            System.out.println("Sleeping...");
                            Thread.sleep(100);
                            if (obj instanceof LinkedTreeMap) {
                                LinkedTreeMap<?,?> linkedTreeMap = (LinkedTreeMap)obj;
                                String kanji = (String)linkedTreeMap.get("kanji");
                                String hiragana = (String)linkedTreeMap.get("hiragana");
                                String nom = (String)linkedTreeMap.get("nom");
                                String meaning = (String)linkedTreeMap.get("meaning");
                                if (kanji != null) {
                                    myTextToSpeechService.processTextToSpeechOrCached(kanji, "ja_jp", "FEMALE");
                                }
                                /*if (hiragana != null) {
                                    myTextToSpeechService.processTextToSpeechOrCached(hiragana, "ja_jp", "FEMALE");
                                }
                                if (nom != null) {
                                    myTextToSpeechService.processTextToSpeechOrCached(nom, "vi_vn", "FEMALE");
                                }
                                if (meaning != null) {
                                    myTextToSpeechService.processTextToSpeechOrCached(meaning, "vi_vn", "FEMALE");
                                }*/
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
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}
