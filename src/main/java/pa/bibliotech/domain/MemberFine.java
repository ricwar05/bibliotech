package pa.bibliotech.domain;
import java.math.BigDecimal;
public record MemberFine(String documentId, String memberName, int finedLoans, BigDecimal
totalFines) {
}
