package ntson.service;

import java.util.Scanner;

public class MyPasswordService {
    public void checkProgramPassword() {
        final String programPassword = "ntson"/*TODO:for testing only!*/;
        System.out.println("Please enter program password:");
        Scanner scanner = new Scanner(System.in);
        String inputPassword = scanner.nextLine();
        if (!programPassword.equals(inputPassword)) {
            System.out.println("Wrong program password! Program now exit!");
            System.exit(0);
        } else {
            System.out.println("Correct program password! Continuing...");
        }
    }
}
