package ntson.model;

import com.google.gson.annotations.Expose;
import ntson.util.StringFancy;

import java.util.ArrayList;
import java.util.List;

public class EnJaSentenceRow {
    @Expose
    public final String lessonId;
    @Expose
    public final String englishRawText;
    @Expose
    public final String japaneseRawText;

    @Expose
    public String englishAudioFileName = null;
    @Expose
    public String japaneseAudioFileName = null;
    @Expose
    public String japaneseRepresentativeText = null;

    @Expose
    public List<SentenceInfo> englishSentences = null;
    @Expose
    public List<SentenceInfo> japaneseSentences = null;

    private String englishTrimmedText = null;
    private String japaneseNoSpaceText = null;

    public String getJapaneseNoSpaceText() {
        if (this.japaneseNoSpaceText == null) {
            this.japaneseNoSpaceText = this.japaneseRawText.trim()
                    .replace(StringFancy.SPACE, StringFancy.EMPTY)
                    .replace(StringFancy.SPACE_JP, StringFancy.EMPTY);
        }
        return this.japaneseNoSpaceText;
    }
    public String getEnglishTrimmedText() {
        if (this.englishTrimmedText == null) {
            this.englishTrimmedText = this.englishRawText.trim();
        }
        return this.englishTrimmedText;
    }

    public EnJaSentenceRow addSentenceEnglish(String rawText, String audioFileName) {
        if (this.englishSentences == null) {
            this.englishSentences = new ArrayList<>();
        }
        this.englishSentences.add(new SentenceInfo(rawText, audioFileName));
        return this;
    }
    public EnJaSentenceRow addSentenceJapanese(String rawText, String audioFileName) {
        if (this.japaneseSentences == null) {
            this.japaneseSentences = new ArrayList<>();
        }
        this.japaneseSentences.add(new SentenceInfo(rawText, audioFileName));
        return this;
    }

    public EnJaSentenceRow(String lessonId, String englishRawText, String japaneseRawText) {
        this.lessonId = lessonId;
        this.englishRawText = englishRawText;
        this.japaneseRawText = japaneseRawText;
    }
    @Override
    public String toString() {
        return "EnJaSentenceRow{"+"lessonId='"+lessonId+'\''+
                ", englishTrimmedText='"+this.getEnglishTrimmedText()+'\''+
                ", japaneseNoSpaceText='"+this.getJapaneseNoSpaceText()+'\''+
                '}';
    }
    public static class SentenceInfo {
        @Expose
        public String rawText;
        @Expose
        public String audioFileName;
        public SentenceInfo(String rawText, String audioFileName) {
            this.rawText = rawText;
            this.audioFileName = audioFileName;
        }
    }
}
