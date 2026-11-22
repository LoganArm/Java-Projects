/** Thrown when an action breaks a library rule (for example, borrowing with an unpaid fine). */
public class LibraryException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** Creates an exception with a message explaining which rule was broken. */
    public LibraryException(String message) {
        super(message);
    }
}
