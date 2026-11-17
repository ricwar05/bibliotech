package pa.bibliotech.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import pa.bibliotech.domain.Loan;

public interface LoanRepository {
    Loan save(Loan loan);
    Optional<Loan> findById(long id);
    List<Loan> findAll();
    List<Loan> findActive();
    List<Loan> findOverdueByMember(long memberId, LocalDate today);
    void update(Loan loan);
}