import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A registered library member, with the books they hold and the fines they owe. */
public class Member {
    private final int memberId;
    private final String name;
    private final String email;
    private final List<Loan> activeLoans = new ArrayList<>();
    private final List<Fine> fines = new ArrayList<>();

    /** Creates a member. Use Library.registerMember so IDs stay unique. */
    public Member(int memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    /** Returns the total of all fines this member has not paid yet. */
    public double unpaidTotal() {
        double sum = 0;
        for (Fine fine : fines) {
            if (!fine.isPaid()) {
                sum += fine.getAmount();
            }
        }
        return Math.round(sum * 100) / 100.0;
    }

    /** Returns true if the member is under the loan limit and owes nothing. */
    public boolean canBorrow() {
        return activeLoans.size() < Library.MAX_LOANS && unpaidTotal() == 0;
    }

    void addLoan(Loan loan) { activeLoans.add(loan); }
    void removeLoan(Loan loan) { activeLoans.remove(loan); }
    void addFine(Fine fine) { fines.add(fine); }

    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Loan> getActiveLoans() { return Collections.unmodifiableList(activeLoans); }
    public List<Fine> getFines() { return Collections.unmodifiableList(fines); }

    @Override
    public String toString() {
        return "#" + memberId + " " + name + " <" + email + ">";
    }
}
