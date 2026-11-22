/** A late fee charged to a member for one overdue loan. */
public class Fine {
    private final int fineId;
    private final Member member;
    private final Loan loan;
    private final double amount;
    private boolean paid;

    /** Creates an unpaid fine. Use Library.returnBook, which creates fines automatically. */
    public Fine(int fineId, Member member, Loan loan, double amount) {
        this.fineId = fineId;
        this.member = member;
        this.loan = loan;
        this.amount = amount;
    }

    /** Marks this fine as paid. */
    void markPaid() {
        paid = true;
    }

    public int getFineId() { return fineId; }
    public Member getMember() { return member; }
    public Loan getLoan() { return loan; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return paid; }

    @Override
    public String toString() {
        return String.format("Fine %d: $%.2f for %s (%s)", fineId, amount,
                loan.getBook().getTitle(), paid ? "paid" : "unpaid");
    }
}
