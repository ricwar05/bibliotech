public interface LoanRepository {
    Loan save(Loan loan);
    Optional<Loan> findById(long id);
    List<Loan> findAll();
    List<Loan> findActive();
    List<Loan> findOverdueByMember(long memberId, LocalDate today);
    void update(Loan loan);
}