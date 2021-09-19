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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static ntson.service.MyFileService.buildAudioFilePath;

public class MyTextToSpeechServicePremium {
    private static final String VOICE_NAME_FEMALE_JP_WAVENET_B = "ja-JP-Wavenet-B";
    private static final Logger logger = LoggerFactory.getLogger(MyTextToSpeechService.class);

    final private TextToSpeechClient textToSpeechClient;

    public MyTextToSpeechServicePremium(TextToSpeechClient textToSpeechClientInput) {
        this.textToSpeechClient = textToSpeechClientInput;
    }

    private String textToSpeechWaveNetJp(
            final String text,
            final AudioEncoding audioEncoding
    ) {
        try {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
            
            // Build the voice request, select the language code ("en-US") and the ssml voice gender ("neutral")
            VoiceSelectionParams voiceSelectionParams = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(LanguageCode.JA_JP.value)
                    .setName(VOICE_NAME_FEMALE_JP_WAVENET_B)
                    .build();
            
            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(audioEncoding).build();
            
            // Perform the text-to-speech request on the text input with the selected voice parameters and audio file type
            SynthesizeSpeechResponse response
                    = textToSpeechClient.synthesizeSpeech(input, voiceSelectionParams, audioConfig);
            
            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();
            
            // Write the response to the output file.
            String filePathStr = buildAudioFilePath(text, LanguageCode.JA_JP, SsmlVoiceGender.FEMALE, audioEncoding);
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

    public String processTextToSpeechOrCachedWaveNetJp(
            final String text,
            final AudioEncoding audioEncoding
    ) {
        final String filePathStr = buildAudioFilePath(text, LanguageCode.JA_JP, SsmlVoiceGender.FEMALE, audioEncoding);
        boolean isFileExist = FileUtil.isFileExist(filePathStr);
        if (isFileExist) {
            logger.info("File with path {} exists! Now returning that path!", filePathStr);
            return filePathStr;
        } else {
            logger.info("File with path {} NOT exists! Now calling Google TTS API!", filePathStr);
            String filePathStrTTS = textToSpeechWaveNetJp(text, audioEncoding);
            if (filePathStrTTS != null && !filePathStr.isEmpty()) {
                return filePathStr;
            } else {
                return null;
            }
        }
    }
    public String processTextToSpeechOrCachedWaveNetJp(
            final String text
    ) {
        return processTextToSpeechOrCachedWaveNetJp(text, AudioEncoding.MP3);
    }

    public String preprocessJapaneseText(String jpText) {
        return jpText.replace(" ", "");
    }
}
