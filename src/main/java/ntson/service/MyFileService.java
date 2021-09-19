package ntson.service;

import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import ntson.enums.LanguageCode;

public class MyFileService {
    private static final char[] ILLEGAL_CHARS = {'\\','/',':','*','?','"','<','>','|','\n','\r','\t'};
    public static boolean isTextContainsIllegalChar(String text) {
        for (char illegalChar : ILLEGAL_CHARS) {
            if (text.indexOf(illegalChar) >= 0) {
                return true;
            }
        }
        return false;
    }
    public static String buildAudioFilePath(
            final String text,
            final LanguageCode languageCode,
            final SsmlVoiceGender voiceGender,
            final AudioEncoding audioEncoding
    ) {
        final String fileExtensionWithDot = "." + buildFileExtensionFromAudioEncoding(audioEncoding);
        if (isTextContainsIllegalChar(text)) {
            return "audio/"+voiceGender.name()+"/"+languageCode.name()+"/"+(text.hashCode())+fileExtensionWithDot;
        }
        return "audio/"+voiceGender.name()+"/"+languageCode.name()+"/"+text+fileExtensionWithDot;
    }
    public static String buildAudioFilePath(
            final String text,
            final LanguageCode languageCode,
            final SsmlVoiceGender voiceGender
    ) {
        return buildAudioFilePath(text, languageCode, voiceGender, AudioEncoding.MP3);
    }
    public static String buildFileExtensionFromAudioEncoding(AudioEncoding audioEncoding) {
        switch (audioEncoding) {
            case MP3: return "mp3";
            case OGG_OPUS: return "ogg";
            case LINEAR16: return "wav";
            default: return null;
        }
    }
}