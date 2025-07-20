package io.corementor.mindexpanse.dto;




public class AuthResponse {
    private String names;
    private String email;
    private String token;



    public AuthResponse() {
    }

    public AuthResponse(String names, String email, String token  ) {
        this.names = names;
        this.email = email;
        this.token = token;

    }

    public AuthResponse(Object o, Object o1, Object o2,  String invalidUsernameOrPassword) {
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
