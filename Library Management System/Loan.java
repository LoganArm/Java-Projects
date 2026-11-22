import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** One checkout of one book by one member. */
public class Loan {
    private final int loanId;
    private final Book book;
    private final Member member;
    private final LocalDate checkoutDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    /** Creates a loan. Use Library.checkoutBook so the rules are checked first. */
    public Loan(int loanId, Book book, Member member, LocalDate checkoutDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.book = book;
        this.member = member;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
    }

    /** Returns true once the book has been brought back. */
    public boolean isReturned() {
        return returnDate != null;
    }

    /**
     * Counts the days this loan is (or was) late.
     *
     * @param onDate the date to measure against if the book is still out
     * @return days past the due date, or 0 if on time
     */
    public int daysOverdue(LocalDate onDate) {
        LocalDate end = (returnDate != null) ? returnDate : onDate;
        return (int) Math.max(ChronoUnit.DAYS.between(dueDate, end), 0);
    }

    /** Marks the book as returned on the given date. */
    void markReturned(LocalDate date) {
        this.returnDate = date;
    }

    public int getLoanId() { return loanId; }
    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    @Override
    public String toString() {
        return "Loan " + loanId + ": " + book.getTitle() + " -> " + member.getName()
                + " (due " + dueDate + (isReturned() ? ", returned " + returnDate : "") + ")";
    }
}
