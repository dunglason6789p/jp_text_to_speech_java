package ntson.model;

import com.google.gson.annotations.Expose;
import ntson.util.StringFancy;

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
}
