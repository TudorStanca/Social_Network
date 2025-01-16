package domain.dto;

public class UserDTO {

    private Long id = null;
    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private String password = null;
    private String imagePath = null;

    public UserDTO(Long id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public UserDTO(Long id, String firstName, String lastName, String imagePath) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imagePath = imagePath;
    }

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getImagePath() {
        return imagePath;
    }
}
