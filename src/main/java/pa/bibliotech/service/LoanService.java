package pa.bibliotech.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import pa.bibliotech.domain.Book;
import pa.bibliotech.domain.Loan;
import pa.bibliotech.domain.Member;
import pa.bibliotech.exception.BusinessRuleException;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.exception.EntityNotFoundException;
import pa.bibliotech.repository.BookRepository;
import pa.bibliotech.repository.LoanRepository;
import pa.bibliotech.repository.MemberRepository;
import pa.bibliotech.strategy.FineStrategy;

public class LoanService {
    private static final Logger LOGGER = Logger.getLogger(LoanService.class.getName());
    private static final int LOAN_DAYS = 14;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final FineStrategy fineStrategy;
    private final Connection connection;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository,
            MemberRepository memberRepository, FineStrategy fineStrategy, Connection connection) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.fineStrategy = fineStrategy;
        this.connection = connection;
    }

    public List<Loan> listActive() {
        return loanRepository.findActive();
    }

    public Loan registerLoan(long bookId, long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("No existe el socio con id " + memberId));
        if (!member.isActive()) {
            throw new BusinessRuleException("El socio " + member.getFullName() + " está inactivo");
        }
        LocalDate today = LocalDate.now();
        if (!loanRepository.findOverdueByMember(memberId, today).isEmpty()) {
            throw new BusinessRuleException("El socio tiene un préstamo vencido sin devolver");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No existe el libro con id " + bookId));
        if (!book.isAvailable()) {
            throw new BusinessRuleException("No hay ejemplares disponibles de " + book.getTitle());
        }
        Loan loan = new Loan(0, bookId, memberId, today, today.plusDays(LOAN_DAYS), null, BigDecimal.ZERO);
        Loan saved = inTransaction(() -> {
            Loan created = loanRepository.save(loan);
            book.decreaseAvailableCopies();
            bookRepository.update(book);
            return created;
        });
        LOGGER.info("Préstamo " + saved.getId() + " registrado: libro " + bookId + ", socio " + memberId);
        return saved;
    }

    private <T> T inTransaction(Supplier<T> work) {
        try {
            connection.setAutoCommit(false);
            T result = work.get();
            connection.commit();
            return result;
        } catch (SQLException e) {
            rollbackQuietly();
            throw new DataAccessException("No se pudo completar la operación", e);
        } catch (RuntimeException e) {
            rollbackQuietly();
            throw e;
        } finally {
            restoreAutoCommit();
        }
    }

    private void rollbackQuietly() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "No se pudo deshacer la transacción", e);
        }
    }

    private void restoreAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "No se pudo restaurar el autoCommit", e);
        }
    }
}