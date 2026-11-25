package pa.bibliotech.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import pa.bibliotech.domain.Book;
import pa.bibliotech.exception.BusinessRuleException;
import pa.bibliotech.repository.BookRepository;

class BookServiceTest {
    private final FakeBookRepository repository = new FakeBookRepository();
    private final BookService service = new BookService(repository);

    @Test
    void isbnConLetrasSeRechaza() {
        assertThrows(BusinessRuleException.class,
                () -> service.register("97899ABC001", "Ficciones", "Borges", 1944, 2));
    }

    @Test
    void isbnConGuionesSeGuardaSoloConDigitos() {
        Book saved = service.register("978-9962-00-001-1", "Ficciones", "Borges", 1944, 2);
        assertEquals("9789962000011", saved.getIsbn());
    }

    @Test
    void anioFueraDeRangoSeRechaza() {
        assertThrows(BusinessRuleException.class,
                () -> service.register("9789962000011", "Ficciones", "Borges", 1200, 2));
    }

    @Test
    void tituloDeMasDe200CaracteresSeRechaza() {
        String longTitle = "a".repeat(201);
        assertThrows(BusinessRuleException.class,
                () -> service.register("9789962000011", longTitle, "Borges", 1944, 2));
    }

    @Test
    void isbnRepetidoSeRechaza() {
        service.register("9789962000011", "Ficciones", "Borges", 1944, 2);
        assertThrows(BusinessRuleException.class,
                () -> service.register("9789962000011", "Otro", "Otro", 2000, 1));
    }

    private static class FakeBookRepository implements BookRepository {
        private final Map<Long, Book> books = new HashMap<>();
        private long nextId = 1;

        @Override
        public Optional<Book> findById(long id) {
            return Optional.ofNullable(books.get(id));
        }

        @Override
        public Optional<Book> findByIsbn(String isbn) {
            return books.values().stream().filter(b -> b.getIsbn().equals(isbn)).findFirst();
        }

        @Override
        public List<Book> findAll() {
            return new ArrayList<>(books.values());
        }

        @Override
        public List<Book> search(String text) {
            return findAll();
        }

        @Override
        public Book save(Book book) {
            book.setId(nextId++);
            books.put(book.getId(), book);
            return book;
        }

        @Override
        public void update(Book book) {
            books.put(book.getId(), book);
        }

        @Override
        public void deleteById(long id) {
            books.remove(id);
        }
    }
}