package pa.bibliotech.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pa.bibliotech.domain.Loan;

class FineStrategyTest {
    private final Loan loan = new Loan(1, 1, 1, LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 10, 15), null, BigDecimal.ZERO);

    @Test
    void sinRetrasoNoHayMulta() {
        FineStrategy strategy = new PerDayFineStrategy(new BigDecimal("0.50"));
        assertEquals(0, strategy.calculate(loan, LocalDate.of(2026, 10,
                15)).compareTo(BigDecimal.ZERO));
    }

    @Test
    void multaPorDiaDeRetraso() {
        FineStrategy strategy = new PerDayFineStrategy(new BigDecimal("0.50"));
        assertEquals(0, strategy.calculate(loan, LocalDate.of(2026, 10, 19)).compareTo(new BigDecimal("2.00")));
    }

    @Test
    void multaConTopeNoPasaDelMaximo() {
        FineStrategy strategy = new CappedFineStrategy(new BigDecimal("0.50"), new BigDecimal("3.00"));
        assertEquals(0, strategy.calculate(loan, LocalDate.of(2026, 11, 4)).compareTo(new BigDecimal("3.00")));
    }
}