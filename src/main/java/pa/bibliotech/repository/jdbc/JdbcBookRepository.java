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
import pa.bibliotech.domain.Book;
import pa.bibliotech.exception.BusinessRuleException;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.exception.EntityNotFoundException;
import pa.bibliotech.repository.BookRepository;

/** Implementación JDBC: el único lugar con SQL de libros. Patrón Repository/DAO. */
public class JdbcBookRepository implements BookRepository {
    private static final Logger LOGGER = Logger.getLogger(JdbcBookRepository.class.getName());
    private static final String COLUMNS =
            "id, isbn, title, author, publication_year, total_copies, available_copies";
    private final Connection connection;

    public JdbcBookRepository(Connection connection) {     // la conexión llega desde fuera
        this.connection = connection;
    }

    @Override
    public Optional<Book> findById(long id) {
        String sql = "SELECT " + COLUMNS + " FROM books WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar el libro " + id, e);
            throw new DataAccessException("No se pudo consultar el libro", e);
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT " + COLUMNS + " FROM books WHERE isbn = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar el ISBN " + isbn, e);
            throw new DataAccessException("No se pudo consultar el libro", e);
        }
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM books ORDER BY title";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Book> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar libros", e);
            throw new DataAccessException("No se pudo listar los libros", e);
        }
    }

    @Override
    public List<Book> search(String text) {
        String sql = "SELECT " + COLUMNS + " FROM books "
                   + "WHERE title ILIKE ? OR author ILIKE ? ORDER BY title";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String pattern = "%" + text + "%";              // el % va en el parámetro, no en el SQL
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            List<Book> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error en la búsqueda de libros", e);
            throw new DataAccessException("No se pudo buscar libros", e);
        }
    }

    @Override
    public Book save(Book book) {
        String sql = "INSERT INTO books (isbn, title, author, publication_year, total_copies, "
                   + "available_copies) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getPublicationYear());
            ps.setInt(5, book.getTotalCopies());
            ps.setInt(6, book.getAvailableCopies());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                book.setId(rs.getLong("id"));
                return book;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar el libro " + book.getIsbn(), e);
            throw new DataAccessException("No se pudo guardar el libro", e);
        }
    }

    @Override
    public void update(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, publication_year = ?, "
                   + "total_copies = ?, available_copies = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.getPublicationYear());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());
            ps.setLong(6, book.getId());
            if (ps.executeUpdate() == 0) {
                throw new EntityNotFoundException("No existe el libro con id " + book.getId());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar el libro " + book.getId(), e);
            throw new DataAccessException("No se pudo actualizar el libro", e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0) {
                throw new EntityNotFoundException("No existe el libro con id " + id);
            }
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {          // el libro tiene préstamos
                throw new BusinessRuleException("No se puede eliminar: el libro tiene préstamos registrados");
            }
            LOGGER.log(Level.SEVERE, "Error al eliminar el libro " + id, e);
            throw new DataAccessException("No se pudo eliminar el libro", e);
        }
    }

    /** Convierte una fila de la tabla en un objeto Book. */
    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(rs.getLong("id"), rs.getString("isbn"), rs.getString("title"),
                rs.getString("author"), rs.getInt("publication_year"),
                rs.getInt("total_copies"), rs.getInt("available_copies"));
    }
}