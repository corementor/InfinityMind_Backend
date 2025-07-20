package io.corementor.mindexpanse.service;

import io.corementor.mindexpanse.dto.AuthResponse;
import io.corementor.mindexpanse.dto.LoginDto;
import io.corementor.mindexpanse.dto.Userdto;

public interface IAuthenticationService {
    AuthResponse registerUser(Userdto userdto);
    AuthResponse authenticate(LoginDto loginDto);
}
