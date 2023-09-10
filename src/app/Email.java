package app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import static app.EmailUtils.readEmail;
import static app.EmailUtils.writeEmailToFile;

/**
 * Contains user info such as: first- and lastname, department, email, password and alternative email.
 * Allows a user to set/change password and alternative email.
 */
public class Email implements Serializable {

    public static final String CAPITAL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String SMALL_CHARS = "abcdefghijklmnopqrstuvwxyz";
    public static final String NUMBERS = "0123456789";
    public static final String SYMBOLS = "!@#$%&?";
    /**
     * Regex for email validation
     */
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._]+@\\w{3,}\\.(org|com)$";
    public static final int INITIAL_PASS_LENGTH = 8;

    public transient Scanner scanner = new Scanner(System.in);
    private String firstName;
    private String lastName;
    private String dept;
    private String email;
    private String password;
    private int mailCapacity = 500;
    private String alterEmail;

    /**
     * Create new email.
     * @param firstName user's first name.
     * @param lastName user's last name
     */
    public Email(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        System.out.printf("New user: %s %s\n",this.firstName,this.lastName);
        this.dept = this.setDept();
        this.password = this.generatePass(INITIAL_PASS_LENGTH);
        this.email = this.generateEmail();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(email, email.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }


    /**
     * Generate user's email depending on the firstname, lastname and department.
     */
    private String generateEmail(){
        return String.format("%s.%s@%s.company.com",
                this.firstName.toLowerCase(),
                this.lastName.toLowerCase(),
                this.dept.toLowerCase());
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDept() {
        return dept;
    }

    public int getMailCapacity() {
        return mailCapacity;
    }

    public String getAlterEmail() {
        return alterEmail;
    }

    /**
     * Set Department value depending on the user's input
     * @return department name
     */
    private String setDept(){
        System.out.println("DEPARTMENTS:");
        Arrays.stream(Departments.values())
                .forEach(department -> System.out.printf("%d. %s%n", department.ordinal() + 1, department));
        while(scanner.hasNext()) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    return Departments.ADMINISTRATION.toString();
                }
                case 2 -> {
                    return Departments.DEVELOPMENT.toString();
                }
                case 3 -> {
                    return Departments.ACCOUNTING.toString();
                }
                case 4 -> {
                    return Departments.SALES.toString();
                }
                case 0 -> {
                    return Departments.NONE.toString();
                }
                default -> System.out.println("**INVALID CHOICE** \nEnter the Department:");
            }
        }
        return null;
    }

    /**
     * Generate a random password for a user.
     * @param length length of a password.
     * @return password
     */
    private String generatePass(int length){
        Random random = new Random();
        String chars = CAPITAL_CHARS + SMALL_CHARS + NUMBERS + SYMBOLS;
        return random.ints(length, 0, chars.length())
                .mapToObj(chars::charAt)
                .collect(StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append)
                .toString();
    }
    /**
     * Let user change the password.
     */
    public void changePass(){
        boolean flag = false;
        do {
            System.out.print("ARE YOU SURE YOU WANT TO CHANGE YOUR PASSWORD? (Y/N) : ");
            char choice = scanner.next().charAt(0); //in case user prints YES
            switch (choice) {
                case 'Y', 'y' -> {
                    flag = true;
                    System.out.print("Enter your current password: ");
                    String temp = scanner.next();
                    if (temp.equals(this.password)) {
                        System.out.print("Enter the new password: ");
                        this.password = scanner.next();
                        System.out.println("PASSWORD CHANGED SUCCESSFULLY!");
                    } else {
                        System.out.println("INCORRECT PASSWORD!");
                    }
                }
                case 'N', 'n' -> {
                    flag = true;
                    System.out.println("PASSWORD CHANGE CANCELED!");
                }
                default -> System.out.println("**ENTER A VALID CHOICE**");
            }
        } while (!flag);
    }

    /**
     * Let user enter new Email capacity.
     */
    public void setMailCapacity() {
        System.out.printf("Current capacity = %dmb%n",this.mailCapacity);

        int newCapacity = -1;
        boolean validInput = false;
        while (!validInput) {
            System.out.print("Enter new capacity: ");
            String input = scanner.next();

            try {
                newCapacity = Integer.parseInt(input);
                if (newCapacity >= 0) {
                    validInput = true;
                } else {
                    System.out.println("Capacity cannot be negative. Please enter a non-negative value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer value.");
            }
        }

        this.mailCapacity = newCapacity;
        System.out.println("MAILBOX CAPACITY CHANGED SUCCESSFULLY!");
    }

    /**
     * Let user change the alternate Email.
     */
    public void setAlterEmail() {
        boolean validInput = false;
        String newAlternateEmail = "";

        while (!validInput) {
            System.out.print("Enter new alternate email: ");
            newAlternateEmail = scanner.next();

            if (isValid(newAlternateEmail)) {
                validInput = true;
            } else {
                System.out.println("Invalid email format. Please enter a valid email address.");
            }
        }

        this.alterEmail = newAlternateEmail;
        System.out.println("ALTERNATE EMAIL SET SUCCESSFULLY!");
    }

    /**
     * Validate Email.
     * @param email Email input.
     * @return true if Email resembles the pattern {@value #EMAIL_PATTERN} , otherwise false.
     */
    public static boolean isValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        return pattern.matcher(email).matches();
    }

    /**
     * Display information about the user.
     */
    public void getInfo() {
        System.out.printf("NAME: %s %s%n", firstName, lastName);
        System.out.printf("DEPARTMENT: %s%n", dept);
        System.out.printf("EMAIL: %s%n", email);
        System.out.printf("PASSWORD: %s%n", password);
        System.out.printf("MAILBOX CAPACITY: %dmb%n", mailCapacity);
        System.out.printf("ALTERNATIVE EMAIL: %s%n", (alterEmail == null || alterEmail.isEmpty()) ? "none" : alterEmail);
    }

    /**
     * Read an Email-object from the file system.
     * @param filePath a path to the file with emails data in the file system.
     */
    public void readFile(Path filePath) {
        Email email = readEmail(this, filePath);
        if(email == null) {
            System.out.println("Email not found.");
            return;
        }
        this.firstName = email.getFirstName();
        this.lastName = email.getLastName();
        this.email = email.getEmail();
        this.dept = email.getDept();
        this.password = email.getPassword();
        this.alterEmail = email.getAlterEmail();
        System.out.println("Email read from file and updated.");
    }

    /**
     * Store an object to the file system.
     * @param filePath a path to the file with emails data in the file system.
     */
    public void storeFile(Path filePath) {
        writeEmailToFile(filePath, this);
    }

}
