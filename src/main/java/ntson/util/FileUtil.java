package ntson.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FileUtil {
    public static boolean isFileExist(String filePathStr) {
        File f = new File(filePathStr);
        return f.exists() && !f.isDirectory();
    }
    /**Mimic Java 11 Files.readString(String path, Charset encoding) .*/
    public static String readEntireTextFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }
    public static void createDirectoriesOptional(String pathStr, BiConsumer<IOException, String> ioExceptionHandler) {
        try {
            Files.createDirectories(Paths.get(pathStr));
        } catch (IOException e) {
            if (ioExceptionHandler != null) {
                ioExceptionHandler.accept(e, pathStr);
            }
        }
    }
    public static void createDirectoriesOptional(String pathStr) throws IOException {
        Files.createDirectories(Paths.get(pathStr));
    }
}
