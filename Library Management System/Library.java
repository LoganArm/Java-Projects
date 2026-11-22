import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Central class: owns all books, members, loans and fines.
 *
 * Rules:
 *   - A loan lasts LOAN_DAYS days.
 *   - A late return costs FINE_PER_DAY dollars for each day overdue.
 *   - A member may hold at most MAX_LOANS books at once.
 *   - A member with an unpaid fine cannot check out new books.
 */
public class Library {
    public static final int LOAN_DAYS = 14;
    public static final double FINE_PER_DAY = 0.25;
    public static final int MAX_LOANS = 5;

    private final Map<String, Book> books = new LinkedHashMap<>();
    private final Map<Integer, Member> members = new LinkedHashMap<>();
    private final Map<Integer, Loan> loans = new LinkedHashMap<>();
    private final Map<Integer, Fine> fines = new LinkedHashMap<>();
    private int nextMemberId = 1;
    private int nextLoanId = 1;
    private int nextFineId = 1;

    // ---- catalog ----

    /**
     * Adds a book to the catalog. If the ISBN already exists, the copies are added to it.
     *
     * @throws IllegalArgumentException if copies is less than 1
     */
    public Book addBook(String isbn, String title, String author, int copies) {
        Book existing = books.get(isbn);
        if (existing != null) {
            if (copies < 1) {
                throw new IllegalArgumentException("copies must be at least 1");
            }
            existing.addCopies(copies);
            return existing;
        }
        Book book = new Book(isbn, title, author, copies);
        books.put(isbn, book);
        return book;
    }

    /** Finds books whose title or author contains the text (ignoring upper/lower case). */
    public List<Book> searchBooks(String query) {
        String q = query.toLowerCase();
        List<Book> found = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(q)
                    || book.getAuthor().toLowerCase().contains(q)) {
                found.add(book);
            }
        }
        return found;
    }

    // ---- members ----

    /** Registers a new member and gives them the next ID number. */
    public Member registerMember(String name, String email) {
        Member member = new Member(nextMemberId++, name, email);
        members.put(member.getMemberId(), member);
        return member;
    }

    private Member findMember(int memberId) {
        Member member = members.get(memberId);
        if (member == null) {
            throw new LibraryException("Unknown member: " + memberId);
        }
        return member;
    }

    // ---- checkouts ----

    /** Checks out a book to a member, starting today. */
    public Loan checkoutBook(String isbn, int memberId) {
        return checkoutBook(isbn, memberId, LocalDate.now());
    }

    /**
     * Checks out a book to a member on the given date.
     *
     * @throws LibraryException if the member or book is unknown, the member owes fines
     *         or is at the loan limit, or no copy is available
     */
    public Loan checkoutBook(String isbn, int memberId, LocalDate onDate) {
        Member member = findMember(memberId);
        Book book = books.get(isbn);
        if (book == null) {
            throw new LibraryException("Unknown book: " + isbn);
        }
        if (member.unpaidTotal() > 0) {
            throw new LibraryException("Member has unpaid fines");
        }
        if (member.getActiveLoans().size() >= MAX_LOANS) {
            throw new LibraryException("Member has reached the loan limit");
        }
        if (!book.isAvailable()) {
            throw new LibraryException("No copies available");
        }
        Loan loan = new Loan(nextLoanId++, book, member, onDate, onDate.plusDays(LOAN_DAYS));
        book.takeCopy();
        member.addLoan(loan);
        loans.put(loan.getLoanId(), loan);
        return loan;
    }

    /** Returns a book today. */
    public Optional<Fine> returnBook(int loanId) {
        return returnBook(loanId, LocalDate.now());
    }

    /**
     * Returns a book on the given date.
     *
     * @return the new Fine if the book was late, otherwise empty
     * @throws LibraryException if the loan is unknown or already returned
     */
    public Optional<Fine> returnBook(int loanId, LocalDate onDate) {
        Loan loan = loans.get(loanId);
        if (loan == null) {
            throw new LibraryException("Unknown loan: " + loanId);
        }
        if (loan.isReturned()) {
            throw new LibraryException("Loan already returned");
        }
        loan.markReturned(onDate);
        loan.getBook().returnCopy();
        loan.getMember().removeLoan(loan);
        int daysLate = loan.daysOverdue(onDate);
        if (daysLate == 0) {
            return Optional.empty();
        }
        double amount = Math.round(daysLate * FINE_PER_DAY * 100) / 100.0;
        Fine fine = new Fine(nextFineId++, loan.getMember(), loan, amount);
        fines.put(fine.getFineId(), fine);
        loan.getMember().addFine(fine);
        return Optional.of(fine);
    }

    // ---- fines ----

    /**
     * Records payment of a fine.
     *
     * @throws LibraryException if the fine is unknown or already paid
     */
    public Fine payFine(int fineId) {
        Fine fine = fines.get(fineId);
        if (fine == null) {
            throw new LibraryException("Unknown fine: " + fineId);
        }
        if (fine.isPaid()) {
            throw new LibraryException("Fine already paid");
        }
        fine.markPaid();
        return fine;
    }

    // ---- lookups ----

    public Book getBook(String isbn) { return books.get(isbn); }
    public Member getMember(int memberId) { return members.get(memberId); }
    public Loan getLoan(int loanId) { return loans.get(loanId); }
    public Fine getFine(int fineId) { return fines.get(fineId); }
}
