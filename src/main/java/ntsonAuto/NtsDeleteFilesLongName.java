package ntsonAuto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class NtsDeleteFilesLongName {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Files.walk(Paths.get("audio"))
                .forEach(path -> {
                    if (path != null) {
                        String fileName = path.getFileName().toString();
                        //System.out.println(fileName);
                        if (fileName != null && fileName.length() > 200) {
                            System.out.println("Wanna delete this file? " + fileName);
                            String answer = scanner.nextLine();
                            if (answer != null && answer.equalsIgnoreCase("y")) {
                                File file = path.toFile();
                                try {
                                    boolean isDeletedSuccessfully = file.delete();
                                    System.out.println(isDeletedSuccessfully ? "OK!" : "Oops!");
                                } catch (Exception e) {
                                    System.err.println(e);
                                }
                            }
                        }
                    }
                });
    }
}
