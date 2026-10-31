package pa.bibliotech.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import pa.bibliotech.domain.Loan;

public class PerDayFineStrategy implements FineStrategy {
    private final BigDecimal ratePerDay;

    public PerDayFineStrategy(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    @Override
    public BigDecimal calculate(Loan loan, LocalDate returnDate) {
        long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
        return overdueDays <= 0 ? BigDecimal.ZERO
                : ratePerDay.multiply(BigDecimal.valueOf(overdueDays));
    }
}