package pa.bibliotech.repository;

import java.util.List;
import java.util.Optional;
import pa.bibliotech.domain.Member;

public interface MemberRepository {
    Optional<Member> findById(long id);
    Optional<Member> findByDocumentId(String documentId);
    List<Member> findAll();
    List<Member> search(String text);
    Member save(Member member);
    void update(Member member);
    void deleteById(long id);
    int countActiveLoans(long memberId);
}
