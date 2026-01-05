package Model;


public class User {
    private String userId;
    private String username;
    private String password;
    private String role;
    private String referenceId;  //  patient_id /  staff_id

    public User() {}

    public User(String userId, String username, String password, String role, String referenceId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.referenceId = referenceId;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
}
