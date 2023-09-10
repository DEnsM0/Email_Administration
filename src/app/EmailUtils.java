package app;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A util class.
 * Allows user interaction and file management.
 */
public final class EmailUtils {
    /**
     * Forbids an instantiation of the util class EmailUtils.
     * Throw an exception if an instantiation is attempted.
     */
    private EmailUtils() {
        throw new AssertionError("EmailUtils is a util class and cannot be instantiated.");
    }

    /**
     * Provide a UI for user interactions through the console.
     * @param scanner a scanner to read user's input
     * @param email a specific instance of the Email-class
     * @param path a path to the file with emails data in the file system.
     */
    public static void handleUserInteractions(Scanner scanner, Email email, Path path) {
        int choice;
        do {
            System.out.println("\n**********\nENTER YOUR CHOICE\n"
                    + "1. Show Info\n"
                    + "2. Change Password\n"
                    + "3. Change Mailbox Capacity\n"
                    + "4. Set Alternate Email\n"
                    + "5. Read Data from a File\n"
                    + "6. Store Data in File\n"
                    + "7. Exit");

            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> email.getInfo();
                case 2 -> email.changePass();
                case 3 -> email.setMailCapacity();
                case 4 -> email.setAlterEmail();
                case 5 -> email.readFile(path);
                case 6 -> email.storeFile(path);
                case 7 -> System.out.println("\nThank you for using our service.");
                default -> System.out.println("INVALID CHOICE! ENTER AGAIN!");
            }
        } while (choice != 7);
    }

    /**
     * Read a list of Email-objects from the file system.
     * @param filePath a path to the file with emails data in the file system.
     * @return a list of all emails stored in the file system.
     */
    private static List<Email> readFromFile(Path filePath) {
        List<Email> emails = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            while (true) {
                try {
                    emails = (List<Email>) ois.readObject();
                } catch (EOFException e) {
                    // End of file reached
                    break;
                }
            }
        } catch (EOFException e) {
            // File is empty, return empty list
            return emails;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading emails: " + e.getMessage());
        }

        return emails;
    }

    /**
     * Find a specific email.
     * @param emails a list of Email-objects.
     * @param email a String with an email address provided by the user.
     * @param password a String with a password provided by the user.
     * @return a specific Email-object.
     */
    private static Email findEmail(List<Email> emails, String email, String password) {
        for (Email e : emails) {
            if (e.getEmail().equals(email) && e.getPassword().equals(password)) {
                return e;
            }
        }
        return null;
    }

    /**
     * read a specific email from the file system.
     * @param email a specific instance of the Email-class
     * @param path a path to the file with emails data in the file system.
     * @return a specific Email-object.
     */
    public static Email readEmail(Email email, Path path){
        return findEmail(readFromFile(path), email.getEmail(), email.getPassword());
    }

    /**
     * Let the user enter an email address and password,
     * and find a specific Email-object in the file system using th entered data.
     * @param scanner a scanner to read user's input
     * @param path a path to the file with emails data in the file system.
     * @return a specific Email-object, null if a specified email non-existent.
     */
    public static Email loadExistingUser(Scanner scanner, Path path) {
        List<Email> emails = readFromFile(path);
        if(emails.isEmpty()){
            return null;
        }

        String email = null;
        String password = null;

        while(true){
            System.out.print("Enter your email: ");
            email = scanner.next();
            if(!Email.isValid(email)) {
                System.out.println("Invalid email!");
            } else {
                break;
            }
        }
        System.out.print("Enter your password: ");
        password = scanner.next();


        return findEmail(emails, email, password);
    }

    /**
     * Let user enter an email address and password,
     * and create a new instance of the Email-class using th entered data.
     * @param scanner a scanner to read user's input
     * @return a new Email-object, created using the data entered by the user.
     */
    public static Email createNewUser(Scanner scanner) {
        System.out.print("Enter your firstname: ");
        String firstName = scanner.next();

        System.out.print("Enter your lastname: ");
        String lastName = scanner.next();

        return new Email(firstName, lastName);
    }

    /**
     * Save an instance of the Email-class to the file system.
     * @param filePath a path to the file with emails data in the file system.
     * @param newEmail an Email-object to be saved to the file system.
     */
    public static void writeEmailToFile(Path filePath, Email newEmail) {
        createFile(filePath);
        List<Email> emails = readFromFile(filePath); // Read existing emails from file

        boolean emailExists = false;
        for (int i = 0; i < emails.size(); i++) {
            if (emails.get(i).getEmail().equals(newEmail.getEmail())) {
                emails.set(i, newEmail); // Overwrite existing email
                emailExists = true;
                break;
            }
        }

        if (!emailExists) {
            emails.add(newEmail);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            // Write the list of emails back to the file
            oos.writeObject(emails);
            System.out.println("Email written to file.");
        } catch (IOException e) {
            System.out.println("Error writing email to file: " + e.getMessage());
        }
    }

    /**
     * Create a new file in the file system to save emails.
     * @param filePath a path to the file with emails data in the file system.
     */
    public static void createFile(Path filePath) {
        Path directoryPath = filePath.getParent();
        try {
            // Create the directory if it doesn't exist
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            // Create the file if it doesn't exist
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.out.println("Error creating the directory or the file: " + e.getMessage());
        }
    }
}
