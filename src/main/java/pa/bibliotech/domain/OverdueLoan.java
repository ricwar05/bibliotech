package pa.bibliotech.domain;
import java.time.LocalDate;
public record OverdueLoan(long loanId, String bookTitle, String memberName, LocalDate dueDate)
{
}