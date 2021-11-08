package akatsuki.restaurantsysteminformation.unregistereduser.dto;

import akatsuki.restaurantsysteminformation.enums.UserType;

public class UnregisteredUserTableDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String type;

    public UnregisteredUserTableDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type.toString().toLowerCase();
    }
}
