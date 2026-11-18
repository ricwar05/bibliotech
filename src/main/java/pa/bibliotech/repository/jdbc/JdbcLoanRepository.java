package pa.bibliotech.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import pa.bibliotech.domain.Loan;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.exception.EntityNotFoundException;
import pa.bibliotech.repository.LoanRepository;

public class JdbcLoanRepository implements LoanRepository {
    private static final Logger LOGGER = Logger.getLogger(JdbcLoanRepository.class.getName());
    private static final String COLUMNS =
            "id, book_id, member_id, loan_date, due_date, return_date, fine_amount";
    private final Connection connection;

    public JdbcLoanRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Loan save(Loan loan) {
        String sql = "INSERT INTO loans (book_id, member_id, loan_date, due_date, fine_amount) "
                   + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, loan.getBookId());
            ps.setLong(2, loan.getMemberId());
            ps.setObject(3, loan.getLoanDate());
            ps.setObject(4, loan.getDueDate());
            ps.setBigDecimal(5, loan.getFineAmount());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                loan.setId(rs.getLong("id"));
                return loan;
            }
        } catch (SQLException e) {
            throw translate(e, "guardar el préstamo");
        }
    }

    @Override
    public Optional<Loan> findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM loans WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw translate(e, "consultar el préstamo " + id);
        }
    }

    @Override
    public List<Loan> findAll() {
        return query("SELECT " + COLUMNS + " FROM loans ORDER BY loan_date DESC", "listar préstamos");
    }

    @Override
    public List<Loan> findActive() {
        return query("SELECT " + COLUMNS + " FROM loans WHERE return_date IS NULL ORDER BY due_date",
                "listar préstamos activos");
    }

    @Override
    public List<Loan> findOverdueByMember(long memberId, LocalDate today) {
        String sql = "SELECT " + COLUMNS + " FROM loans "
                   + "WHERE member_id = ? AND return_date IS NULL AND due_date < ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            ps.setObject(2, today);
            return readAll(ps);
        } catch (SQLException e) {
            throw translate(e, "buscar préstamos vencidos del socio " + memberId);
        }
    }

    @Override
    public void update(Loan loan) {
        String sql = "UPDATE loans SET return_date = ?, fine_amount = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, loan.getReturnDate());
            ps.setBigDecimal(2, loan.getFineAmount());
            ps.setLong(3, loan.getId());
            if (ps.executeUpdate() == 0) {
                throw new EntityNotFoundException("No existe el préstamo con id " + loan.getId());
            }
        } catch (SQLException e) {
            throw translate(e, "actualizar el préstamo " + loan.getId());
        }
    }

    private List<Loan> query(String sql, String action) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return readAll(ps);
        } catch (SQLException e) {
            throw translate(e, action);
        }
    }

    private List<Loan> readAll(PreparedStatement ps) throws SQLException {
        List<Loan> result = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    private RuntimeException translate(SQLException e, String action) {
        LOGGER.log(Level.SEVERE, "Error al " + action, e);
        return new DataAccessException("No se pudo " + action, e);
    }

    private Loan mapRow(ResultSet rs) throws SQLException {
        return new Loan(
                rs.getLong("id"),
                rs.getLong("book_id"),
                rs.getLong("member_id"),
                rs.getObject("loan_date", LocalDate.class),
                rs.getObject("due_date", LocalDate.class),
                rs.getObject("return_date", LocalDate.class),
                rs.getBigDecimal("fine_amount"));
    }
}