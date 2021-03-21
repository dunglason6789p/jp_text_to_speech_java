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
import org.springframework.stereotype.Service;

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
    public void textToSpeech(final String text) {
        // Set the text input to be synthesized
        SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
        
        // Build the voice request, select the language code ("en-US") and the ssml voice gender ("neutral")
        VoiceSelectionParams voice =
                VoiceSelectionParams.newBuilder()
                        .setLanguageCode("en-US")
                        .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                        .build();
        
        // Select the type of audio file you want returned
        AudioConfig audioConfig =
                AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();
        
        // Perform the text-to-speech request on the text input with the selected voice parameters and
        // audio file type
        SynthesizeSpeechResponse response =
                textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
        
        // Get the audio contents from the response
        ByteString audioContents = response.getAudioContent();
        
        // Write the response to the output file.
        try (OutputStream out = new FileOutputStream("output.mp3")) {
            out.write(audioContents.toByteArray());
            System.out.println("Audio content written to file \"output.mp3\"");
        }
    }
}
