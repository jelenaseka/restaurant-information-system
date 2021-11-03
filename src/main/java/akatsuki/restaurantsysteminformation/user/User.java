package akatsuki.restaurantsysteminformation.user;

import akatsuki.restaurantsysteminformation.enums.UserType;

import javax.persistence.*;

@Entity
@Table(name = "MyUser")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email_address", unique = true, nullable = false)
    private String emailAddress;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "salary", nullable = false)
    private double salary;

    @Column(name = "type", nullable = false)
    private UserType type;

    @Column(name = "is_deleted", nullable = false)
    private boolean idDeleted;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String emailAddress, String phoneNumber, double salary, UserType type, boolean idDeleted) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.salary = salary;
        this.type = type;
        this.idDeleted = idDeleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public boolean isIdDeleted() {
        return idDeleted;
    }

    public void setIdDeleted(boolean idDeleted) {
        this.idDeleted = idDeleted;
    }
}
