package pa.bibliotech.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import pa.bibliotech.domain.Member;
import pa.bibliotech.exception.BusinessRuleException;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.exception.EntityNotFoundException;
import pa.bibliotech.repository.MemberRepository;

public class JdbcMemberRepository implements MemberRepository {
    private static final Logger LOGGER = Logger.getLogger(JdbcMemberRepository.class.getName());
    private static final String COLUMNS = "id, document_id, full_name, email, phone, active";
    private final Connection connection;

    public JdbcMemberRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Member> findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM members WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw translate(e, "consultar el socio " + id);
        }
    }

    @Override
    public Optional<Member> findByDocumentId(String documentId) {
        String sql = "SELECT " + COLUMNS + " FROM members WHERE document_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, documentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw translate(e, "consultar el documento " + documentId);
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM members ORDER BY full_name";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Member> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw translate(e, "listar socios");
        }
    }

    @Override
    public List<Member> search(String text) {
        String sql = "SELECT " + COLUMNS + " FROM members "
                   + "WHERE full_name ILIKE ? OR document_id ILIKE ? ORDER BY full_name";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String pattern = "%" + text + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            List<Member> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw translate(e, "buscar socios");
        }
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO members (document_id, full_name, email, phone, active) "
                   + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, member.getDocumentId());
            ps.setString(2, member.getFullName());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());
            ps.setBoolean(5, member.isActive());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                member.setId(rs.getLong("id"));
                return member;
            }
        } catch (SQLException e) {
            throw translate(e, "guardar el socio " + member.getDocumentId());
        }
    }

    @Override
    public void update(Member member) {
        String sql = "UPDATE members SET full_name = ?, email = ?, phone = ?, active = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, member.getFullName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setBoolean(4, member.isActive());
            ps.setLong(5, member.getId());
            if (ps.executeUpdate() == 0) {
                throw new EntityNotFoundException("No existe el socio con id " + member.getId());
            }
        } catch (SQLException e) {
            throw translate(e, "actualizar el socio " + member.getId());
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0) {
                throw new EntityNotFoundException("No existe el socio con id " + id);
            }
        } catch (SQLException e) {
            throw translate(e, "eliminar el socio " + id);
        }
    }

    @Override
    public int countActiveLoans(long memberId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE member_id = ? AND return_date IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw translate(e, "contar los préstamos del socio " + memberId);
        }
    }

    private RuntimeException translate(SQLException e, String action) {
        if ("23505".equals(e.getSQLState())) {
            return new BusinessRuleException("El documento o el correo ya están registrados");
        }
        if ("23503".equals(e.getSQLState())) {
            return new BusinessRuleException("No se puede eliminar: el socio tiene préstamos registrados");
        }
        LOGGER.log(Level.SEVERE, "Error al " + action, e);
        return new DataAccessException("No se pudo " + action, e);
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(rs.getLong("id"), rs.getString("document_id"), rs.getString("full_name"),
                rs.getString("email"), rs.getString("phone"), rs.getBoolean("active"));
    }
}
