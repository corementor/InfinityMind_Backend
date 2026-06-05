package io.corementor.infinitymind.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * The Class UserDto.
 * @author Blaise Mugisha
 * @version 1.0
 */
@Getter
@Setter
public class UserDto {
    /**
     * The first name.
     */
    private String firstName;
    /**
     * The last name.
     */
    private String lastName;
    /**
     * The email.
     */
    private String email;
    /**
     * The username.
     */
    private String username;
    /**
     * The password.
     */
    private String password;

    public UserDto(String firstName, String lastName, String email, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public UserDto() {

    }

}
