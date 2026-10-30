package pa.bibliotech.domain;

import pa.bibliotech.exception.BusinessRuleException;

/** Un libro del catálogo. Protege sus propias reglas (encapsulamiento). */
public class Book {
    private long id;
    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private int totalCopies;
    private int availableCopies;

    /** Libro nuevo: todos sus ejemplares empiezan disponibles. */
    public Book(String isbn, String title, String author, int publicationYear, int copies) {
        this(0, isbn, title, author, publicationYear, copies, copies);
    }

    /** Libro leído de la base de datos. */
    public Book(long id, String isbn, String title, String author,
                int publicationYear, int totalCopies, int availableCopies) {
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new BusinessRuleException("Los ejemplares disponibles están fuera de rango");
        }
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /** Se usará al registrar un préstamo (fase siguiente). */
    public void decreaseAvailableCopies() {
        if (!isAvailable()) {
            throw new BusinessRuleException("No hay ejemplares disponibles de " + title);
        }
        availableCopies--;
    }

    public void updateDetails(String title, String author, int publicationYear) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    /** Cambia el total sin perder la cuenta de los ejemplares prestados. */
    public void changeTotalCopies(int newTotal) {
        int lent = totalCopies - availableCopies;
        if (newTotal < lent) {
            throw new BusinessRuleException("No puede haber menos ejemplares (" + newTotal
                    + ") que los prestados (" + lent + ")");
        }
        this.availableCopies = newTotal - lent;
        this.totalCopies = newTotal;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getPublicationYear() { return publicationYear; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
}