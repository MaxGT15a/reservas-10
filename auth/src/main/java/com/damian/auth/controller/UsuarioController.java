package com.damian.auth.controller;

import com.pedromax.commons.controller.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.damian.auth.dto.UsuarioRequest;
import com.damian.auth.dto.UsuarioResponse;
import com.damian.auth.services.UsuarioService;

@RestController
@RequestMapping("/admin/usuarios")
public class UsuarioController extends CrudController<UsuarioRequest, UsuarioResponse, UsuarioService> {

    public UsuarioController(UsuarioService service) {
        super(service);
    }
}
