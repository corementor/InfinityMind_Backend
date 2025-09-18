package io.corementor.infinitymind.service;

import io.corementor.infinitymind.dto.AuthResponse;
import io.corementor.infinitymind.dto.LoginDto;
import io.corementor.infinitymind.dto.Userdto;

public interface IAuthenticationService {
    AuthResponse registerUser(Userdto userdto);
    AuthResponse authenticate(LoginDto loginDto);
}
