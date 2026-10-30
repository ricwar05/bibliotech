package pa.bibliotech.domain;

public class Member {
    private long id;
    private final String documentId;
    private String fullName;
    private String email;
    private String phone;
    private boolean active;

    public Member(String documentId, String fullName, String email, String phone) {
        this(0, documentId, fullName, email, phone, true);
    }

    public Member(long id, String documentId, String fullName, String email,
                  String phone, boolean active) {
        this.id = id;
        this.documentId = documentId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.active = active;
    }

    public void updateContact(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getDocumentId() { return documentId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isActive() { return active; }
}
