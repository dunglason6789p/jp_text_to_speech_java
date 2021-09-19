package ntson.service;

// Imports the Google Cloud client library
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import ntson.enums.LanguageCode;
import ntson.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class MyTextToSpeechService {
    private static final String CREDENTIAL_FILE_PATH = "credentials/main-project--credential--owner.json";
    private static final Logger logger = LoggerFactory.getLogger(MyTextToSpeechService.class);

    final private TextToSpeechClient textToSpeechClient;
    public TextToSpeechClient getTextToSpeechClient() {
        return this.textToSpeechClient;
    }

    public MyTextToSpeechService() {
        try {
            FileInputStream credentialFileInputStream = new FileInputStream(CREDENTIAL_FILE_PATH);
            Credentials credentials = ServiceAccountCredentials.fromStream(credentialFileInputStream);
            CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);
            TextToSpeechSettings textToSpeechSettings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .build();
            this.textToSpeechClient = TextToSpeechClient.create(textToSpeechSettings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final char[] ILLEGAL_CHARS = {'\\','/',':','*','?','"','<','>','|','\n','\r','\t'};
    private boolean isTextContainsIllegalChar(String text) {
        for (char illegalChar : ILLEGAL_CHARS) {
            if (text.indexOf(illegalChar) >= 0) {
                return true;
            }
        }
        return false;
    }

    private String buildAudioFilePath(
            final String text,
            final LanguageCode languageCode,
            final SsmlVoiceGender voiceGender
    ) {
        if (isTextContainsIllegalChar(text)) {
            return "audio/"+voiceGender.name()+"/"+languageCode.name()+"/"+(text.hashCode())+".mp3";
        }
        return "audio/"+voiceGender.name()+"/"+languageCode.name()+"/"+text+".mp3";
    }

    private String textToSpeech(
            final String text,
            final LanguageCode languageCode,
            final SsmlVoiceGender voiceGender,
            final AudioEncoding audioEncoding
    ) {
        try {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
            
            // Build the voice request, select the language code ("en-US") and the ssml voice gender ("neutral")
            VoiceSelectionParams voiceSelectionParams = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(languageCode.value)
                    .setSsmlGender(voiceGender)
                    .build();
            
            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(audioEncoding).build();
            
            // Perform the text-to-speech request on the text input with the selected voice parameters and audio file type
            SynthesizeSpeechResponse response
                    = textToSpeechClient.synthesizeSpeech(input, voiceSelectionParams, audioConfig);
            
            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();
            
            // Write the response to the output file.
            String filePathStr = buildAudioFilePath(text, languageCode, voiceGender);
            try (OutputStream out = new FileOutputStream(filePathStr)) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file:"+filePathStr);
                return filePathStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String processTextToSpeechOrCached(
            final String text,
            final String languageCodeString,
            final String voiceGenderString
    ) {
        LanguageCode languageCode = LanguageCode.fromString(languageCodeString);
        SsmlVoiceGender voiceGender = SsmlVoiceGender.valueOf(voiceGenderString.toUpperCase());
        String filePathStr = buildAudioFilePath(text, languageCode, voiceGender);
        boolean isFileExist = FileUtil.isFileExist(filePathStr);
        if (isFileExist) {
            logger.info("File with path {} exists! Now returning that path!", filePathStr);
            return filePathStr;
        } else {
            logger.info("File with path {} NOT exists! Now calling Google TTS API!", filePathStr);
            String filePathStrTTS = textToSpeech(text, languageCode, voiceGender, AudioEncoding.MP3);
            if (filePathStrTTS != null && !filePathStr.isEmpty()) {
                return filePathStr;
            } else {
                return null;
            }
        }
    }
    public String processTextToSpeechOrCachedV2(
            final String textInput,
            final LanguageCode languageCode,
            final boolean isFemale
    ) {
        String text;
        if (languageCode == LanguageCode.VI_VN) {
            text = textInput.trim().toLowerCase();
        } else {
            text = textInput;
        }
        SsmlVoiceGender voiceGender = isFemale ? SsmlVoiceGender.FEMALE : SsmlVoiceGender.MALE;
        String filePathStr = buildAudioFilePath(text, languageCode, voiceGender);
        boolean isFileExist = FileUtil.isFileExist(filePathStr);
        if (isFileExist) {
            logger.info("File with path {} exists! Now returning that path!", filePathStr);
            return filePathStr;
        } else {
            logger.info("File with path {} NOT exists! Now calling Google TTS API!", filePathStr);
            String filePathStrTTS = textToSpeech(text, languageCode, voiceGender, AudioEncoding.MP3);
            if (filePathStrTTS != null && !filePathStr.isEmpty()) {
                return filePathStr;
            } else {
                return null;
            }
        }
    }
    public String processTextToSpeechOrCachedV2(
            final String textInput,
            final LanguageCode languageCode
    ) {
        return this.processTextToSpeechOrCachedV2(textInput, languageCode, true);
    }
}
