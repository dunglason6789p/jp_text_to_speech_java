package ntson.model;

import ntson.util.StringFancy;

public class EnJaSentenceRow {
    public final String lessonId;
    public final String englishRawText;
    public final String japaneseRawText;

    public String japaneseRepresentative = null;

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
