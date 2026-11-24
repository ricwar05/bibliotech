package pa.bibliotech.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pa.bibliotech.domain.BookLoanCount;
import pa.bibliotech.domain.MemberFine;
import pa.bibliotech.domain.OverdueLoan;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.repository.ReportRepository;

public class JdbcReportRepository implements ReportRepository {

    private static final Logger LOGGER =
            Logger.getLogger(JdbcReportRepository.class.getName());

    private final Connection connection;

    public JdbcReportRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<BookLoanCount> topBorrowedBooks(int limit) {
        String sql = "SELECT b.title, b.author, COUNT(l.id) AS times_loaned "
                + "FROM loans l JOIN books b ON b.id = l.book_id "
                + "GROUP BY b.id, b.title, b.author "
                + "ORDER BY times_loaned DESC, b.title "
                + "LIMIT ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            List<BookLoanCount> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new BookLoanCount(
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("times_loaned")));
                }
            }
            return result;
        } catch (SQLException e) {
            throw translate(e, "consultar los libros más prestados");
        }
    }

    @Override
    public List<OverdueLoan> overdueLoans(LocalDate today) {
        String sql = "SELECT l.id, b.title, m.full_name, l.due_date "
                + "FROM loans l "
                + "JOIN books b ON b.id = l.book_id "
                + "JOIN members m ON m.id = l.member_id "
                + "WHERE l.return_date IS NULL AND l.due_date < ? "
                + "ORDER BY l.due_date";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, today);
            List<OverdueLoan> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new OverdueLoan(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("full_name"),
                            rs.getObject("due_date", LocalDate.class)));
                }
            }
            return result;
        } catch (SQLException e) {
            throw translate(e, "consultar los préstamos vencidos");
        }
    }

    @Override
    public List<MemberFine> finesByMember() {
        String sql = "SELECT m.document_id, m.full_name, COUNT(l.id) AS fined_loans, "
                + "SUM(l.fine_amount) AS total_fines "
                + "FROM loans l JOIN members m ON m.id = l.member_id "
                + "WHERE l.fine_amount > 0 "
                + "GROUP BY m.id, m.document_id, m.full_name "
                + "ORDER BY total_fines DESC, m.full_name";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<MemberFine> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new MemberFine(
                        rs.getString("document_id"),
                        rs.getString("full_name"),
                        rs.getInt("fined_loans"),
                        rs.getBigDecimal("total_fines")));
            }
            return result;
        } catch (SQLException e) {
            throw translate(e, "consultar las multas por socio");
        }
    }

    private RuntimeException translate(SQLException e, String action) {
        LOGGER.log(Level.SEVERE, "Error al " + action, e);
        return new DataAccessException("No se pudo " + action, e);
    }
}
