package org.test.mindexpanseweb.service;

import org.test.mindexpanseweb.dto.AuthResponse;
import org.test.mindexpanseweb.dto.LoginDto;
import org.test.mindexpanseweb.dto.Userdto;

public interface IAuthenticationService {
    AuthResponse registerUser(Userdto userdto);
    AuthResponse authenticate(LoginDto loginDto);
}
