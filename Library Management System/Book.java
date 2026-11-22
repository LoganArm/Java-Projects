/** A title in the library catalog, with a count of physical copies. */
public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private int copiesTotal;
    private int copiesAvailable;

    /**
     * Creates a book.
     *
     * @param copies number of copies owned (at least 1)
     * @throws IllegalArgumentException if copies is less than 1
     */
    public Book(String isbn, String title, String author, int copies) {
        if (copies < 1) {
            throw new IllegalArgumentException("copies must be at least 1");
        }
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copiesTotal = copies;
        this.copiesAvailable = copies;
    }

    /** Returns true if at least one copy is on the shelf. */
    public boolean isAvailable() {
        return copiesAvailable > 0;
    }

    /** Adds more copies of this book to the catalog. */
    void addCopies(int count) {
        copiesTotal += count;
        copiesAvailable += count;
    }

    /** Takes one copy off the shelf (used at checkout). */
    void takeCopy() {
        copiesAvailable--;
    }

    /** Puts one copy back on the shelf (used at return). */
    void returnCopy() {
        copiesAvailable++;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getCopiesTotal() { return copiesTotal; }
    public int getCopiesAvailable() { return copiesAvailable; }

    @Override
    public String toString() {
        return title + " by " + author + " (ISBN " + isbn + ", "
                + copiesAvailable + "/" + copiesTotal + " available)";
    }
}
