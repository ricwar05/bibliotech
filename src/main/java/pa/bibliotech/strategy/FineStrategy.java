package pa.bibliotech.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

import pa.bibliotech.domain.Loan;

public interface FineStrategy {
    BigDecimal calculate(Loan loan, LocalDate returnDate);
}