package ntson.service;

// Imports the Google Cloud client library
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import ntson.enums.LanguageCode;
import ntson.util.FileUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class MyTextToSpeechService {
    final private TextToSpeechClient textToSpeechClient;

    public MyTextToSpeechService() {
        try {
            this.textToSpeechClient = TextToSpeechClient.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean textToSpeech(
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
            try (OutputStream out = new FileOutputStream("output.mp3")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output.mp3\"");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildAudioFilePath(
            final String text,
            final LanguageCode languageCode,
            final SsmlVoiceGender voiceGender
    ) {
        return "audio/"+voiceGender.name()+"/"+languageCode.name()+"/"+text+".mp3";
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
            return filePathStr;
        } else {
            boolean isTTSSuccessful = textToSpeech(text, languageCode, voiceGender, AudioEncoding.MP3);
            if (isTTSSuccessful) {
                return filePathStr;
            } else {
                return null;
            }
        }
    }
}
