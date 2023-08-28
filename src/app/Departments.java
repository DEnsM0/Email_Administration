package app;
/**
 * Defines the various departments.
 * Gives their corresponding String representation
 */
public enum Departments {
    ADMINISTRATION("Administration"),
    DEVELOPMENT("Development"),
    ACCOUNTING("Accounting"),
    SALES("Sales"),
    NONE("None");

    private  final String string;

    Departments(String string) {
        this.string = string;
    }


    @Override
    public String toString() {
        return string;
    }
}
