import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for the Library Management System.
 * Plain Java (no extra libraries needed). Run with:
 *   javac *.java
 *   java LibraryTests
 */
public class LibraryTests {
    private static final LocalDate DAY1 = LocalDate.of(2026, 1, 1); // due Jan 15

    private static Library lib;
    private static Member ana;

    private static int passed = 0;
    private static int failed = 0;

    /** Builds a fresh library before every test. */
    private static void setUp() {
        lib = new Library();
        lib.addBook("111", "Dune", "Frank Herbert", 2);
        lib.addBook("222", "Emma", "Jane Austen", 1);
        ana = lib.registerMember("Ana", "ana@example.com");
    }

    // ---------- catalog ----------

    static void addBookSetsAvailableCopies() {
        assertEquals(2, lib.getBook("111").getCopiesAvailable());
    }

    static void addDuplicateIsbnAddsCopies() {
        lib.addBook("111", "Dune", "Frank Herbert", 3);
        assertEquals(5, lib.getBook("111").getCopiesTotal());
        assertEquals(5, lib.getBook("111").getCopiesAvailable());
    }

    static void zeroCopiesRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> lib.addBook("333", "Bad", "Nobody", 0));
    }

    static void searchByTitleIgnoresCase() {
        List<Book> found = lib.searchBooks("dUnE");
        assertEquals(1, found.size());
        assertEquals("111", found.get(0).getIsbn());
    }

    static void searchByAuthor() {
        List<Book> found = lib.searchBooks("austen");
        assertEquals(1, found.size());
        assertEquals("222", found.get(0).getIsbn());
    }

    static void searchWithNoMatchReturnsEmptyList() {
        assertTrue(lib.searchBooks("zebra").isEmpty());
    }

    // ---------- members ----------

    static void memberIdsAreSequential() {
        Member ben = lib.registerMember("Ben", "ben@example.com");
        assertEquals(1, ana.getMemberId());
        assertEquals(2, ben.getMemberId());
    }

    static void unknownMemberCannotCheckout() {
        assertThrows(LibraryException.class, () -> lib.checkoutBook("111", 99, DAY1));
    }

    // ---------- checkout ----------

    static void checkoutReducesCopiesAndSetsDueDate() {
        Loan loan = lib.checkoutBook("111", 1, DAY1);
        assertEquals(1, lib.getBook("111").getCopiesAvailable());
        assertEquals(DAY1.plusDays(Library.LOAN_DAYS), loan.getDueDate());
    }

    static void unknownBookCannotBeCheckedOut() {
        assertThrows(LibraryException.class, () -> lib.checkoutBook("999", 1, DAY1));
    }

    static void checkoutFailsWhenNoCopiesLeft() {
        lib.checkoutBook("222", 1, DAY1);
        Member ben = lib.registerMember("Ben", "ben@example.com");
        assertThrows(LibraryException.class,
                () -> lib.checkoutBook("222", ben.getMemberId(), DAY1));
    }

    static void loanLimitEnforced() {
        fillLoanLimit();
        assertThrows(LibraryException.class, () -> lib.checkoutBook("111", 1, DAY1));
    }

    static void returnFreesLoanSlot() {
        Loan first = fillLoanLimit();
        lib.returnBook(first.getLoanId(), DAY1.plusDays(1));
        lib.checkoutBook("111", 1, DAY1.plusDays(1));
        assertEquals(Library.MAX_LOANS, ana.getActiveLoans().size());
    }

    /** Checks out MAX_LOANS different books to Ana; returns the first loan. */
    private static Loan fillLoanLimit() {
        Loan first = null;
        for (int i = 0; i < Library.MAX_LOANS; i++) {
            lib.addBook("B" + i, "Title " + i, "Author", 1);
            Loan loan = lib.checkoutBook("B" + i, 1, DAY1);
            if (first == null) {
                first = loan;
            }
        }
        return first;
    }

    // ---------- returns and fines ----------

    static void onTimeReturnHasNoFine() {
        Loan loan = lib.checkoutBook("111", 1, DAY1);
        Optional<Fine> fine = lib.returnBook(loan.getLoanId(), DAY1.plusDays(10));
        assertTrue(fine.isEmpty());
        assertEquals(2, lib.getBook("111").getCopiesAvailable());
        assertTrue(ana.getActiveLoans().isEmpty());
    }

    static void returnOnDueDateHasNoFine() {
        Loan loan = lib.checkoutBook("111", 1, DAY1);
        assertTrue(lib.returnBook(loan.getLoanId(), loan.getDueDate()).isEmpty());
    }

    static void returnOneDayLateCostsOneQuarter() {
        Loan loan = lib.checkoutBook("111", 1, DAY1);
        Fine fine = lib.returnBook(loan.getLoanId(), loan.getDueDate().plusDays(1)).get();
        assertEquals(0.25, fine.getAmount());
    }

    static void lateReturnCreatesFine() {
        Loan loan = lib.checkoutBook("111", 1, DAY1);
        Fine fine = lib.returnBook(loan.getLoanId(), LocalDate.of(2026, 1, 25)).get(); // 10 days late
        assertEquals(10 * Library.FINE_PER_DAY, fine.getAmount());
        assertEquals(2.50, ana.unpaidTotal());
    }

    static void doubleReturnRejected() {
        Loan loan = lib.checkoutBook("111", 1, DAY1);
        lib.returnBook(loan.getLoanId(), DAY1);
        assertThrows(LibraryException.class, () -> lib.returnBook(loan.getLoanId(), DAY1));
    }

    static void unknownLoanReturnRejected() {
        assertThrows(LibraryException.class, () -> lib.returnBook(99, DAY1));
    }

    static void unpaidFineBlocksCheckout() {
        makeAnaOweFine();
        assertThrows(LibraryException.class,
                () -> lib.checkoutBook("222", 1, LocalDate.of(2026, 1, 26)));
    }

    static void payingFineRestoresBorrowing() {
        Fine fine = makeAnaOweFine();
        lib.payFine(fine.getFineId());
        assertEquals(0.0, ana.unpaidTotal());
        assertTrue(ana.canBorrow());
        assertTrue(lib.checkoutBook("222", 1, LocalDate.of(2026, 1, 26)) != null);
    }

    static void payingFineTwiceRejected() {
        Fine fine = makeAnaOweFine();
        lib.payFine(fine.getFineId());
        assertThrows(LibraryException.class, () -> lib.payFine(fine.getFineId()));
    }

    static void unknownFinePaymentRejected() {
        assertThrows(LibraryException.class, () -> lib.payFine(99));
    }

    /** Ana borrows "Dune" and returns it 10 days late, so she owes $2.50. */
    private static Fine makeAnaOweFine() {
        Loan loan = lib.checkoutBook("111", 1, DAY1);
        return lib.returnBook(loan.getLoanId(), LocalDate.of(2026, 1, 25)).get();
    }

    // ---------- tiny test framework ----------

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("expected true");
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(double expected, double actual) {
        if (Math.abs(expected - actual) > 0.0001) {
            throw new AssertionError("expected " + expected + " but was " + actual);
        }
    }

    private static void assertThrows(Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                return;
            }
            throw new AssertionError("expected " + type.getSimpleName() + " but got " + t);
        }
        throw new AssertionError("expected " + type.getSimpleName() + " but nothing was thrown");
    }

    private static void run(String name, Runnable test) {
        setUp();
        try {
            test.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("FAIL  " + name + "  ->  " + t.getMessage());
        }
    }

    public static void main(String[] args) {
        run("addBookSetsAvailableCopies", LibraryTests::addBookSetsAvailableCopies);
        run("addDuplicateIsbnAddsCopies", LibraryTests::addDuplicateIsbnAddsCopies);
        run("zeroCopiesRejected", LibraryTests::zeroCopiesRejected);
        run("searchByTitleIgnoresCase", LibraryTests::searchByTitleIgnoresCase);
        run("searchByAuthor", LibraryTests::searchByAuthor);
        run("searchWithNoMatchReturnsEmptyList", LibraryTests::searchWithNoMatchReturnsEmptyList);
        run("memberIdsAreSequential", LibraryTests::memberIdsAreSequential);
        run("unknownMemberCannotCheckout", LibraryTests::unknownMemberCannotCheckout);
        run("checkoutReducesCopiesAndSetsDueDate", LibraryTests::checkoutReducesCopiesAndSetsDueDate);
        run("unknownBookCannotBeCheckedOut", LibraryTests::unknownBookCannotBeCheckedOut);
        run("checkoutFailsWhenNoCopiesLeft", LibraryTests::checkoutFailsWhenNoCopiesLeft);
        run("loanLimitEnforced", LibraryTests::loanLimitEnforced);
        run("returnFreesLoanSlot", LibraryTests::returnFreesLoanSlot);
        run("onTimeReturnHasNoFine", LibraryTests::onTimeReturnHasNoFine);
        run("returnOnDueDateHasNoFine", LibraryTests::returnOnDueDateHasNoFine);
        run("returnOneDayLateCostsOneQuarter", LibraryTests::returnOneDayLateCostsOneQuarter);
        run("lateReturnCreatesFine", LibraryTests::lateReturnCreatesFine);
        run("doubleReturnRejected", LibraryTests::doubleReturnRejected);
        run("unknownLoanReturnRejected", LibraryTests::unknownLoanReturnRejected);
        run("unpaidFineBlocksCheckout", LibraryTests::unpaidFineBlocksCheckout);
        run("payingFineRestoresBorrowing", LibraryTests::payingFineRestoresBorrowing);
        run("payingFineTwiceRejected", LibraryTests::payingFineTwiceRejected);
        run("unknownFinePaymentRejected", LibraryTests::unknownFinePaymentRejected);

        System.out.println();
        System.out.println("Ran " + (passed + failed) + " tests: " + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }
}
