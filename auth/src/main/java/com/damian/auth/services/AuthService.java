package com.damian.auth.services;

import com.damian.auth.dto.LoginRequest;
import com.damian.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}
