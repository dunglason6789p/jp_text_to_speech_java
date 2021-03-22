package ntson.util;

import java.io.File;

public class FileUtil {
    public static boolean isFileExist(String filePathStr) {
        File f = new File(filePathStr);
        return f.exists() && !f.isDirectory();
    }
}
