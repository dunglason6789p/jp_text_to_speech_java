package ntsonAuto.test;

import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import ntson.enums.LanguageCode;
import ntson.service.MyFileService;

public class NtsTestStatic {
    public static void main(String[] args) {
        String fileName = MyFileService.buildAudioFilePath(
                "I love you !,@,#,$,%,^,&,*,(,),=,+,',\",|,/,\\,?,<,>,.,:,; do you know me?",
                LanguageCode.EN_US,
                SsmlVoiceGender.FEMALE,
                AudioEncoding.MP3);
        System.out.println(fileName);
    }
}
