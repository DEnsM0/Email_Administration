package app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static app.EmailUtils.*;

public class EmailRunner {
    public static final String FILE_DIR = "resources";
    public static final String FILE = "emails.emails";

    public static void main(String[] args) {
        Path path = Path.of(FILE_DIR, FILE);

        Scanner scanner = new Scanner(System.in);

        Email email = null;
        boolean flag = false;

        while(!flag) {
            System.out.print("Are you a new user? (Y/N) : ");
            char choice = scanner.next().charAt(0);
            switch (choice) {
                case 'Y', 'y' -> {
                    email = createNewUser(scanner);
                    flag = true;
                }
                case 'N', 'n' -> {
                    if(!Files.exists(path)) {
                        System.out.println("No data found.");
                        break;
                    }
                    email = loadExistingUser(scanner, path);
                    if(email == null) {
                        System.out.println("No such user found");
                    } else {
                        flag = true;
                    }
                }
                default -> System.out.println("**ENTER A VALID CHOICE**");
            }
        }

        handleUserInteractions(scanner, email, path);

        scanner.close();

    }

}
