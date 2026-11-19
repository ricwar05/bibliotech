package pa.bibliotech.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import pa.bibliotech.exception.BusinessRuleException;

class LoanTest {
    private Loan newLoan() {
        return new Loan(1, 1, 1, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 15), null, BigDecimal.ZERO);
    }

    @Test
    void noSePuedeDevolverDosVeces() {
        Loan loan = newLoan();
        loan.registerReturn(LocalDate.of(2026, 10, 10), BigDecimal.ZERO);
        assertThrows(BusinessRuleException.class,
                () -> loan.registerReturn(LocalDate.of(2026, 10, 11), BigDecimal.ZERO));
    }

    @Test
    void estaVencidoDespuesDeLaFechaLimite() {
        Loan loan = newLoan();
        assertFalse(loan.isOverdue(LocalDate.of(2026, 10, 15)));
        assertTrue(loan.isOverdue(LocalDate.of(2026, 10, 16)));
    }

    @Test
    void elVencimientoNoPuedeSerAntesDelPrestamo() {
        assertThrows(BusinessRuleException.class,
                () -> new Loan(1, 1, 1, LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 1), null, BigDecimal.ZERO));
    }
}