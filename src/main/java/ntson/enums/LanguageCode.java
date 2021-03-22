package ntson.enums;

public enum LanguageCode {
    EN_US("en-US"),
    JA_JP("ja-JP"),
    VI_VN("vi-VN"),
    ;
    public final String value;
    private LanguageCode(String value) {
        this.value = value;
    }
    public static LanguageCode fromString(final String languageCodeString) {
        final String languageCodeStringLowerCase = languageCodeString.toLowerCase();
        if (
                languageCodeStringLowerCase.equals("en-us".toLowerCase()) ||
                languageCodeStringLowerCase.equals("en_us".toLowerCase())
        ) {
            return EN_US;
        }
        if (
                languageCodeStringLowerCase.equals("ja-jp".toLowerCase()) ||
                languageCodeStringLowerCase.equals("ja_jp".toLowerCase())
        ) {
            return JA_JP;
        }
        if (
                languageCodeStringLowerCase.equals("vi-vn".toLowerCase()) ||
                languageCodeStringLowerCase.equals("vi_vn".toLowerCase())
        ) {
            return VI_VN;
        }
        throw new IllegalArgumentException("Invalid language-code-string:" + languageCodeString);
    }
}