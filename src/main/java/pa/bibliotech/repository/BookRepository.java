package pa.bibliotech.repository;

import java.util.List;
import java.util.Optional;
import pa.bibliotech.domain.Book;


public interface BookRepository {
    Optional<Book> findById(long id);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findAll();
    List<Book> search(String text);
    Book save(Book book);
    void update(Book book);
    void deleteById(long id);
}