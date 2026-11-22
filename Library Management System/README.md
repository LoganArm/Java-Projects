# Library Management System

Application Development Project (Java): tracks books, members, checkouts, and fines.

## Files

| File | Purpose |
| --- | --- |
| `Library.java` | Main class: catalog, members, checkout, return, fines, and the library rules |
| `Book.java` | A catalog title and its copy counts |
| `Member.java` | A member, with their loans and fines |
| `Loan.java` | One checkout of one book |
| `Fine.java` | A late fee for one overdue loan |
| `LibraryException.java` | Thrown when a rule is broken |
| `LibraryTests.java` | 23 unit tests (plain Java, no extra libraries) |

## Library rules

- A loan lasts 14 days.
- A late return costs $0.25 per day overdue.
- A member can hold at most 5 books at once.
- A member with an unpaid fine cannot check out books.

## Compile and run the tests

Requires Java 11 or newer.

```
javac *.java
java LibraryTests
```

## Example use

```java
import java.time.LocalDate;

public class Demo {
    public static void main(String[] args) {
        Library lib = new Library();
        lib.addBook("111", "Dune", "Frank Herbert", 2);
        Member ana = lib.registerMember("Ana", "ana@example.com");

        Loan loan = lib.checkoutBook("111", ana.getMemberId(), LocalDate.of(2026, 1, 1));
        Fine fine = lib.returnBook(loan.getLoanId(), LocalDate.of(2026, 1, 25)).get(); // 10 days late
        System.out.println(fine.getAmount()); // 2.5
        lib.payFine(fine.getFineId());
    }
}
```

## Not built yet

- User interface (command-line menu)
- Permanent storage (file or database)
- User guide
