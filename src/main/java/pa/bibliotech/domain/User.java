package pa.bibliotech.domain;

public class User {
    private long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String role;
    private boolean active;

    public User(long id, String username, String passwordHash, String fullName, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }
}