package ntson.service;

import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import ntson.enums.LanguageCode;
import ntson.util.struct.MapUtil;
import ntson.util.struct.ValueWrapper;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;

import static ntson.util.FileUtil.createDirectoriesOptional;

public class MyFileService {
    public void dummy() {
        // Why this class is not a pure static utilities?
    }
    public static final String OUTPUT_FOLDER_NAME = "output";
    public static final String OUTPUT_ENJASENTENCES_JSON_FILENAME = "enjasentences.json";
    public static final String OUTPUT_AUDIO_FOLDER_PATH_FEMALE_EN_US = "audio/FEMALE/EN_US";
    public static final String OUTPUT_AUDIO_FOLDER_PATH_FEMALE_JA_JP = "audio/FEMALE/JA_JP";
    public static final String OUTPUT_AUDIO_FOLDER_PATH_FEMALE_VI_VN = "audio/FEMALE/VI_VN";
    private static final Map<Character, String> MAP_ILLEGAL_CHARS
            = MapUtil.MapBuilder.newBuilder(Character.class, String.class)
            // Windows file name illegal chars:
            .put('\\', "{bs}")
            .put('/',  "{fs}")
            .put(':',  "{cl}")
            .put('*',  "{as}")
            .put('?',  "~") // TODO: single char?
            .put('"',  "{qo}")
            .put('<',  "{lt}")
            .put('>',  "{gt}")
            .put('|',  "{vb}")
            .put('\n', "{xn}")
            .put('\r', "{xr}")
            .put('\t', "{xt}")
            .put('.',  "^") // dot is a valid file name char, but we do this to avoid creating wrong file extension. // TODO: single char?
            .put(' ',  "_") // TODO: controversial. // TODO: single char?
            // Web URL special chars:
            .put('$',  "{dl}")
            .put('&',  "{am}")
            .put('+',  "{pl}")
            .put(',',  "`") // TODO: single char?
            .put(';',  "{sc}")
            .put('=',  "{eq}")
            .put('@',  "{at}")
            .put('#',  "{sh}")
            .put('%',  "{pc}")
            // Others
            .put('^',  "{cr}")
            .put('~',  "{ti}")
            .put('[',  "{ls}")
            .put(']',  "{rs}")
            .put('`',  "{ga}")
            .build();
    //private static final char[] ILLEGAL_CHARS = {'\\','/',':','*','?','"','<','>','|','\n','\r','\t'};
    /*
    public static boolean isTextContainsIllegalChar(String text) {
        for (char illegalChar : ILLEGAL_CHARS) {
            if (text.indexOf(illegalChar) >= 0) {
                return true;
            }
        }
        return false;
    }
    */
    private static String replaceIllegalCharsByPredefinedRule(String text) {
        ValueWrapper<String> valueWrapper = new ValueWrapper<>(text);
        MAP_ILLEGAL_CHARS.forEach((illegalChar, encodedPart) -> {
            valueWrapper.replaceBy(oldText -> oldText.replace(illegalChar.toString(), encodedPart));
        });
        return valueWrapper.get();
    }
    public static String buildAudioFilePath(
            final String text,
            final LanguageCode languageCode,
            final SsmlVoiceGender voiceGender,
            final AudioEncoding audioEncoding
    ) {
        final String fileExtensionWithDot = "." + buildFileExtensionFromAudioEncoding(audioEncoding);
        String encodedText = replaceIllegalCharsByPredefinedRule(text);
        return "audio/"+voiceGender.name()+"/"+languageCode.name()+"/"+encodedText+fileExtensionWithDot;
    }
    public static String buildAudioFilePath(
            final String text,
            final LanguageCode languageCode,
            final SsmlVoiceGender voiceGender
    ) {
        return buildAudioFilePath(text, languageCode, voiceGender, AudioEncoding.MP3);
    }
    public static String buildAudioFilePath(
            final String text,
            final LanguageCode languageCode
    ) {
        return buildAudioFilePath(text, languageCode, SsmlVoiceGender.FEMALE, AudioEncoding.MP3);
    }
    private static String buildFileExtensionFromAudioEncoding(AudioEncoding audioEncoding) {
        switch (audioEncoding) {
            case MP3: return "mp3";
            case OGG_OPUS: return "ogg";
            case LINEAR16: return "wav";
            default: return null;
        }
    }
    public static void writeToTextFile(Path filePath, String content) throws IOException {
        Writer writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath.toFile()),
                        StandardCharsets.UTF_8));
        writer.write(content);
        writer.close();
    }
    public static void writeToTextFile(Path filePath, String content, Consumer<IOException> ioExceptionHandler) {
        try {
            writeToTextFile(filePath, content);
        } catch (IOException e) {
            if (ioExceptionHandler != null) {
                ioExceptionHandler.accept(e);
            }
        }
    }
    public static void writeToTextFile(Path folderPath, String fileName, String content) throws IOException {
        createDirectoriesOptional(folderPath.toString());
        Path fullFilePath = folderPath.resolve(fileName);
        writeToTextFile(fullFilePath, content);
    }
}