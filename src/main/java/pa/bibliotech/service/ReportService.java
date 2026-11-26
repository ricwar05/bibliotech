package pa.bibliotech.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import pa.bibliotech.domain.BookLoanCount;
import pa.bibliotech.domain.MemberFine;
import pa.bibliotech.domain.OverdueLoan;
import pa.bibliotech.report.ReportType;
import pa.bibliotech.repository.ReportRepository;

public class ReportService {
    private static final int TOP_BOOKS_LIMIT = 10;

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<String[]> buildRows(ReportType type, LocalDate today) {
        return switch (type) {
            case TOP_BORROWED_BOOKS -> topBorrowedBooks();
            case OVERDUE_LOANS -> overdueLoans(today);
            case MEMBER_FINES -> memberFines();
        };
    }

    private List<String[]> topBorrowedBooks() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[] { "Título", "Autor", "Veces prestado" });
        for (BookLoanCount item : reportRepository.topBorrowedBooks(TOP_BOOKS_LIMIT)) {
            rows.add(new String[] { item.title(), item.author(), String.valueOf(item.timesLoaned()) });
        }
        return rows;
    }

    private List<String[]> overdueLoans(LocalDate today) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[] { "N.º préstamo", "Libro", "Socio", "Vencía", "Días de atraso" });
        for (OverdueLoan loan : reportRepository.overdueLoans(today)) {
            long daysLate = ChronoUnit.DAYS.between(loan.dueDate(), today);
            rows.add(new String[] { String.valueOf(loan.loanId()), loan.bookTitle(), loan.memberName(),
                    loan.dueDate().toString(), String.valueOf(daysLate) });
        }
        return rows;
    }

    private List<String[]> memberFines() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[] { "Documento", "Socio", "Préstamos con multa", "Total multas (B/.)" });
        for (MemberFine fine : reportRepository.finesByMember()) {
            rows.add(new String[] { fine.documentId(), fine.memberName(),
                    String.valueOf(fine.finedLoans()), fine.totalFines().toString() });
        }
        return rows;
    }
}