package pa.bibliotech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import pa.bibliotech.domain.BookLoanCount;
import pa.bibliotech.domain.MemberFine;
import pa.bibliotech.domain.OverdueLoan;
import pa.bibliotech.report.ReportType;
import pa.bibliotech.repository.ReportRepository;

class ReportServiceTest {
    private final ReportService service = new ReportService(new FakeReportRepository());

    @Test
    void prestamosVencidosCalculaLosDiasDeAtraso() {
        List<String[]> rows = service.buildRows(ReportType.OVERDUE_LOANS, LocalDate.of(2026, 11, 19));
        assertEquals(2, rows.size());
        assertEquals("Días de atraso", rows.get(0)[4]);
        assertEquals("3", rows.get(1)[4]);
    }

    @Test
    void multasPorSocioMuestraElTotal() {
        List<String[]> rows = service.buildRows(ReportType.MEMBER_FINES, LocalDate.of(2026, 11, 19));
        assertEquals("1.50", rows.get(1)[3]);
    }

    @Test
    void reporteSinDatosSoloTraeElEncabezado() {
        List<String[]> rows = service.buildRows(ReportType.TOP_BORROWED_BOOKS, LocalDate.of(2026, 11, 19));
        assertEquals(1, rows.size());
    }

    private static class FakeReportRepository implements ReportRepository {
        @Override
        public List<BookLoanCount> topBorrowedBooks(int limit) {
            return List.of();
        }

        @Override
        public List<OverdueLoan> overdueLoans(LocalDate today) {
            return List.of(new OverdueLoan(4, "Introducción a la programación", "María Rodríguez",
                    LocalDate.of(2026, 11, 16)));
        }

        @Override
        public List<MemberFine> finesByMember() {
            return List.of(new MemberFine("PE-333-333", "María Rodríguez", 1, new BigDecimal("1.50")));
        }
    }
}