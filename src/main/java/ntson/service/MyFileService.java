package ntson.service;

import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import ntson.enums.LanguageCode;
import ntson.util.struct.MapUtil;
import ntson.util.struct.ValueWrapper;

import java.util.Map;

public class MyFileService {
    private static final Map<Character, String> MAP_ILLEGAL_CHARS
            = MapUtil.MapBuilder.newBuilder(Character.class, String.class)
            .put('\\', "#bs^")
            .put('/',  "#fs^")
            .put(':',  "#cl^")
            .put('*',  "#as^")
            .put('?',  "#qe^")
            .put('"',  "#qo^")
            .put('<',  "#lt^")
            .put('>',  "#gt^")
            .put('|',  "#vb^")
            .put('.',  "#dt^") // dot is a valid file name char, but we do this to avoid creating wrong file extension.
            .put('\n', "#xn^")
            .put('\r', "#xr^")
            .put('\t', "#xt^")
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
    private static String buildFileExtensionFromAudioEncoding(AudioEncoding audioEncoding) {
        switch (audioEncoding) {
            case MP3: return "mp3";
            case OGG_OPUS: return "ogg";
            case LINEAR16: return "wav";
            default: return null;
        }
    }
}