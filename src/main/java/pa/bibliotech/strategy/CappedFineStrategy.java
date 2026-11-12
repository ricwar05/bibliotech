package pa.bibliotech.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;

import pa.bibliotech.domain.Loan;

public class CappedFineStrategy implements FineStrategy {
    private final FineStrategy perDay;
    private final BigDecimal maxFine;

    public CappedFineStrategy(BigDecimal ratePerDay, BigDecimal maxFine) {
        this.perDay = new PerDayFineStrategy(ratePerDay);
        this.maxFine = maxFine;
    }

    @Override
    public BigDecimal calculate(Loan loan, LocalDate returnDate) {
        return perDay.calculate(loan, returnDate).min(maxFine);
    }
}