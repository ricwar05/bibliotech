package pa.bibliotech.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Loan {
    private long id;
    private long bookId;
    private long memberId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BigDecimal fineAmount;

    public Loan(long id, long bookId, long memberId, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate,
            BigDecimal fineAmount) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }

    public long getId() {
        return id;
    }

    public long getBookId() {
        return bookId;
    }

    public long getMemberId() {
        return memberId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public boolean isOverdue(LocalDate today) {
        return returnDate == null && today.isAfter(dueDate);
    }

    public void registerReturn(LocalDate returnDate, BigDecimal fineAmount) {
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }
}