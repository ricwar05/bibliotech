package pa.bibliotech.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import pa.bibliotech.exception.BusinessRuleException;

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
        if (dueDate.isBefore(loanDate)) {
            throw new BusinessRuleException("El vencimiento no puede ser anterior al préstamo");
        }
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount == null ? BigDecimal.ZERO : fineAmount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isReturned() {
        return returnDate != null;
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
        if (isReturned()) {
            throw new BusinessRuleException("El préstamo ya fue devuelto");
        }
        if (returnDate.isBefore(loanDate)) {
            throw new BusinessRuleException("La devolución no puede ser anterior al préstamo");
        }
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }
}