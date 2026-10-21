package com.damian.auth.services;

import com.damian.auth.dto.UsuarioRequest;
import com.damian.auth.dto.UsuarioResponse;
import com.pedromax.commons.service.CrudService;

public interface UsuarioService extends CrudService<UsuarioRequest, UsuarioResponse> {}
