package pa.bibliotech.repository;
import java.time.LocalDate;
import java.util.List;
import pa.bibliotech.domain.BookLoanCount;
import pa.bibliotech.domain.MemberFine;
import pa.bibliotech.domain.OverdueLoan;
public interface ReportRepository {
 List<BookLoanCount> topBorrowedBooks(int limit);
 List<OverdueLoan> overdueLoans(LocalDate today);
 List<MemberFine> finesByMember();
}