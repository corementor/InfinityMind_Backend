package org.test.mindexpanseweb.dto;


import lombok.Data;

//declaration of variables
@Data
public class Userdto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public Userdto(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
