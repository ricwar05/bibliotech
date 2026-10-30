package pa.bibliotech.service;

import java.util.List;
import java.util.logging.Logger;
import pa.bibliotech.domain.Member;
import pa.bibliotech.exception.BusinessRuleException;
import pa.bibliotech.exception.EntityNotFoundException;
import pa.bibliotech.repository.MemberRepository;

public class MemberService {
    private static final Logger LOGGER = Logger.getLogger(MemberService.class.getName());
    private static final String EMAIL_REGEX = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> listAll() {
        return memberRepository.findAll();
    }

    public List<Member> search(String text) {
        if (text == null || text.isBlank()) {
            return listAll();
        }
        return memberRepository.search(text.trim());
    }

    public Member getById(long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el socio con id " + id));
    }

    public Member register(String documentId, String fullName, String email, String phone) {
        String doc = documentId == null ? "" : documentId.trim().toUpperCase();
        if (!doc.matches("[A-Z0-9-]{5,20}")) {
            throw new BusinessRuleException(
                    "El documento debe tener entre 5 y 20 caracteres (letras, números o guiones)");
        }
        validateContact(fullName, email, phone);
        if (memberRepository.findByDocumentId(doc).isPresent()) {
            throw new BusinessRuleException("Ya existe un socio con el documento " + doc);
        }
        Member saved = memberRepository.save(
                new Member(doc, fullName.trim(), emptyToNull(email), emptyToNull(phone)));
        LOGGER.info("Socio registrado: " + doc);
        return saved;
    }

    public Member update(long id, String fullName, String email, String phone, boolean active) {
        validateContact(fullName, email, phone);
        Member member = getById(id);
        member.updateContact(fullName.trim(), emptyToNull(email), emptyToNull(phone));
        member.setActive(active);
        memberRepository.update(member);
        LOGGER.info("Socio actualizado: " + member.getDocumentId());
        return member;
    }

    public void delete(long id) {
        Member member = getById(id);
        if (memberRepository.countActiveLoans(id) > 0) {
            throw new BusinessRuleException("No se puede eliminar: el socio tiene préstamos activos");
        }
        memberRepository.deleteById(id);
        LOGGER.info("Socio eliminado: " + member.getDocumentId());
    }

    private static void validateContact(String fullName, String email, String phone) {
        if (fullName == null || fullName.isBlank()) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        if (fullName.trim().length() > 120) {
            throw new BusinessRuleException("El nombre no puede pasar de 120 caracteres");
        }
        if (email != null && !email.isBlank() && !email.trim().matches(EMAIL_REGEX)) {
            throw new BusinessRuleException("El correo no tiene un formato válido");
        }
        if (phone != null && !phone.isBlank() && !phone.trim().matches("[0-9+ -]{7,20}")) {
            throw new BusinessRuleException("El teléfono solo admite números, espacios, + y guiones");
        }
    }

    private static String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
