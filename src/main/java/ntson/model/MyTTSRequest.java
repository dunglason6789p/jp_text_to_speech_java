package ntson.model;

import lombok.Data;

@Data
public class MyTTSRequest {
    private String text;
    private String language;
    private String gender;
    public String getLanguageOrDefault() {
        return this.language != null ? this.language : "ja-JP";
    }
    public String getGenderOrDefault() {
        return this.gender != null ? this.gender : "FEMALE";
    }
}
