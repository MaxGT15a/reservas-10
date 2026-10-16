package com.damian.auth.services;

import java.util.Set;

import com.damian.auth.dto.UsuarioRequest;
import com.damian.auth.dto.UsuarioResponse;

public interface UsuarioService {

    Set<UsuarioResponse> listar();
    UsuarioResponse registrar(UsuarioRequest request);
    UsuarioResponse eliminar(String username);
}
