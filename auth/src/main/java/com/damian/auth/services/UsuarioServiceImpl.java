package com.damian.auth.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.RecursoNoEncontradoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.damian.auth.dto.UsuarioRequest;
import com.damian.auth.dto.UsuarioResponse;
import com.damian.auth.entities.Rol;
import com.damian.auth.entities.Usuario;
import com.damian.auth.mapper.UsuarioMapper;
import com.damian.auth.repositories.RolRepository;
import com.damian.auth.repositories.UsuarioRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        log.info("Listando todos los usuarios...");
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::entityToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioMapper.entityToResponse(obtenerUsuario(id));
    }

    @Override
    public UsuarioResponse registrar(UsuarioRequest request) {
        validarUsuarioUnico(request.username());
        Set<Rol> roles = obtenerRoles(request.roles());
        log.info("Registrando usuario con el usuario {}", request.username());

        Usuario usuario = usuarioMapper.requestToEntity(request, passwordEncoder.encode(request.password()), roles);
        usuario = usuarioRepository.saveAndFlush(usuario);

        log.info("Nuevo Usuario registrado con el usuario {}", request.username());
        return usuarioMapper.entityToResponse(usuario);
    }

    @Override
    public UsuarioResponse actualizar(UsuarioRequest request, Long id) {
        Usuario usuario = obtenerUsuario(id);
        validarUsuarioCambioUnico(request.username(), usuario.getUsername());
        Set<Rol> roles = obtenerRoles(request.roles());
        log.info("Actualizando usuario con id: {}", id);

        usuario.actualizar(request.username(), passwordEncoder.encode(request.password()), roles);
        usuarioRepository.saveAndFlush(usuario);

        log.info("Usuario con id: {} actualizado correctamente", id);
        return usuarioMapper.entityToResponse(usuario);
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = obtenerUsuario(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String usuarioAutenticado = authentication.getName();

        if (usuario.getUsername().equals(usuarioAutenticado)) {
            throw new ConflictoException("Los usuarios no pueden eliminarse a si mismos");
        }

        log.info("Eliminando usuario con id: {}", id);
        usuarioRepository.delete(usuario);
        log.info("Usuario con id: {} crucificado correctamente", id);
    }

    private Usuario obtenerUsuario(Long id){
        log.info("Obteniendo usuario con id: {}", id);
        return usuarioRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("No se encontró el usuario con id: " + id));
    }

    private Set<Rol> obtenerRoles(Set<String> roles){
        log.info("Obteniendo roles: {}", roles);
        return roles.stream().map(rol -> rolRepository.findByNombre(rol).orElseThrow(
                () -> new RecursoNoEncontradoException("Rol " + rol + " no encontrado"))).collect(Collectors.toSet());
    }

    private void validarUsuarioUnico(String username) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new ConflictoException("El nombre de usuario " + username + " ya está en uso");
        }
    }

    private void validarUsuarioCambioUnico(String newUsername, String oldUsername) {
        if (usuarioRepository.existsByUsernameAndUsernameNot(newUsername, oldUsername)) {
            throw new ConflictoException("El nombre de usuario " + newUsername + " ya está en uso");
        }
    }
}
