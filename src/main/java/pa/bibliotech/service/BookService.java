package pa.bibliotech.service;

import java.time.Year;
import java.util.List;
import java.util.logging.Logger;
import pa.bibliotech.domain.Book;
import pa.bibliotech.exception.BusinessRuleException;
import pa.bibliotech.exception.EntityNotFoundException;
import pa.bibliotech.repository.BookRepository;


public class BookService {
    private static final Logger LOGGER = Logger.getLogger(BookService.class.getName());
    private final BookRepository bookRepository;              

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> listAll() {
        return bookRepository.findAll();
    }

    public List<Book> search(String text) {
        if (text == null || text.isBlank()) {
            return listAll();
        }
        return bookRepository.search(text.trim());
    }

    public Book getById(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el libro con id " + id));
    }

    public Book register(String isbn, String title, String author, int year, int copies) {
        String cleanIsbn = isbn == null ? "" : isbn.replace("-", "").trim();
        if (!cleanIsbn.matches("\\d{10}|\\d{13}")) {
            throw new BusinessRuleException("El ISBN debe tener 10 o 13 dígitos");
        }
        validateDetails(title, author, year);
        if (copies < 1) {
            throw new BusinessRuleException("Debe registrar al menos un ejemplar");
        }
        if (bookRepository.findByIsbn(cleanIsbn).isPresent()) {
            throw new BusinessRuleException("Ya existe un libro con el ISBN " + cleanIsbn);
        }
        Book saved = bookRepository.save(
                new Book(cleanIsbn, title.trim(), author.trim(), year, copies));
        LOGGER.info("Libro registrado: " + cleanIsbn);
        return saved;
    }

    public Book update(long id, String title, String author, int year, int totalCopies) {
        validateDetails(title, author, year);
        if (totalCopies < 1) {
            throw new BusinessRuleException("Debe haber al menos un ejemplar");
        }
        Book book = getById(id);
        book.updateDetails(title.trim(), author.trim(), year);
        book.changeTotalCopies(totalCopies);
        bookRepository.update(book);
        LOGGER.info("Libro actualizado: " + book.getIsbn());
        return book;
    }

    public void delete(long id) {
        Book book = getById(id);
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new BusinessRuleException("No se puede eliminar: hay ejemplares prestados");
        }
        bookRepository.deleteById(id);
        LOGGER.info("Libro eliminado: " + book.getIsbn());
    }

    private static void validateDetails(String title, String author, int year) {
        if (title == null || title.isBlank()) {
            throw new BusinessRuleException("El título es obligatorio");
        }
        if (author == null || author.isBlank()) {
            throw new BusinessRuleException("El autor es obligatorio");
        }
        if (year < 1450 || year > Year.now().getValue()) {
            throw new BusinessRuleException("El año debe estar entre 1450 y " + Year.now().getValue());
        }
    }
}