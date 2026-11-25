package pa.bibliotech.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import pa.bibliotech.domain.Member;
import pa.bibliotech.exception.BusinessRuleException;
import pa.bibliotech.repository.MemberRepository;

class MemberServiceTest {
    private final MemberService service = new MemberService(new FakeMemberRepository());

    @Test
    void documentoConSimbolosSeRechaza() {
        assertThrows(BusinessRuleException.class,
                () -> service.register("8-123*45", "Ana Pérez", "ana@correo.com", "6000-0000"));
    }

    @Test
    void documentoEnMinusculasSeGuardaEnMayusculas() {
        Member saved = service.register("pe-333-333", "María Rodríguez", "maria@correo.com", "6000-0000");
        assertEquals("PE-333-333", saved.getDocumentId());
    }

    @Test
    void correoSinArrobaSeRechaza() {
        assertThrows(BusinessRuleException.class,
                () -> service.register("8-123-456", "Ana Pérez", "ana.correo.com", "6000-0000"));
    }

    @Test
    void telefonoConLetrasSeRechaza() {
        assertThrows(BusinessRuleException.class,
                () -> service.register("8-123-456", "Ana Pérez", "ana@correo.com", "60AB-0000"));
    }

    private static class FakeMemberRepository implements MemberRepository {
        private final Map<Long, Member> members = new HashMap<>();
        private long nextId = 1;

        @Override
        public Optional<Member> findById(long id) {
            return Optional.ofNullable(members.get(id));
        }

        @Override
        public Optional<Member> findByDocumentId(String documentId) {
            return members.values().stream().filter(m -> m.getDocumentId().equals(documentId)).findFirst();
        }

        @Override
        public List<Member> findAll() {
            return new ArrayList<>(members.values());
        }

        @Override
        public List<Member> search(String text) {
            return findAll();
        }

        @Override
        public Member save(Member member) {
            member.setId(nextId++);
            members.put(member.getId(), member);
            return member;
        }

        @Override
        public void update(Member member) {
            members.put(member.getId(), member);
        }

        @Override
        public void deleteById(long id) {
            members.remove(id);
        }

        @Override
        public int countActiveLoans(long memberId) {
            return 0;
        }
    }
}