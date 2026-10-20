package com.pedromax.huespedes.service;

import com.pedromax.commons.clients.ReservaClient;
import com.pedromax.commons.dto.huespedes.HuespedRequest;
import com.pedromax.commons.dto.huespedes.HuespedResponse;
import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.RecursoNoEncontradoException;
import com.pedromax.huespedes.entity.Huesped;
import com.pedromax.huespedes.mapper.HuespedMapper;
import com.pedromax.huespedes.repository.HuespedRepository;
import com.pedromax.huespedes.utils.ServiceUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HuespedServiceImpl implements HuespedService{
    private final HuespedRepository huespedRepository;
    private final HuespedMapper huespedMapper;
    private final ReservaClient reservaClient;

    @Override
    public HuespedResponse obtenerHuespedPorIdSinEstado(Long id) {
        return huespedMapper.entidadAResponse(obtenerHuesped(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HuespedResponse> listar() {
        log.info("Listando todos los huéspedes...");
        return huespedRepository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(huespedMapper::entidadAResponse).toList();
    }

    @Override
    public HuespedResponse obtenerPorId(Long id) {
        return huespedMapper.entidadAResponse(obtenerHuespedActivo(id));
    }

    @Override
    public HuespedResponse registrar(HuespedRequest request) {
        validarDatosUnicos(request);
        Huesped huesped = huespedMapper.requestAEntidad(request);
        huespedRepository.saveAndFlush(huesped);

        log.info("Huésped registrado con éxito");
        return huespedMapper.entidadAResponse(huesped);
    }

    @Override
    public HuespedResponse actualizar(HuespedRequest request, Long id) {
        Huesped huesped = obtenerHuespedActivo(id);
        validarHuespedActualizarOEliminar(id);
        validarCambiosUnicos(request, id);

        huesped.actualizar(
                request.nombre(),
                request.apellido(),
                request.email(),
                request.telefono(),
                request.numeroDocumento(),
                request.nacionalidad(),
                request.tipoDocumento()
        );
        huespedRepository.saveAndFlush(huesped);
        log.info("Huésped actualizado con éxito");

        return huespedMapper.entidadAResponse(huesped);
    }

    @Override
    public void eliminar(Long id) {
        Huesped huesped = obtenerHuespedActivo(id);
        validarHuespedActualizarOEliminar(id);
        huesped.eliminar();

        huespedRepository.saveAndFlush(huesped);
        log.info("El huésped con id:{} ha sido eliminado 💀", id);
    }

    private Huesped obtenerHuesped(Long id){
        return ServiceUtils.obtenerEntidadOExcepcion(
                huespedRepository,
                id,
                Huesped.class
        );
    }

    private Huesped obtenerHuespedActivo(Long id){
        log.info("Obteniendo huésped activo con id: {}", id);
        return huespedRepository.findByIdAndEstadoRegistro(
                id, EstadoRegistro.ACTIVO
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Huésped no encontrado con id: " + id
                )
        );
    }

    private void validarDatosUnicos(HuespedRequest request){
        if (huespedRepository.existsByEmailIgnoreCaseAndEstadoRegistro(request.email().toLowerCase(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un huésped activo con el mismo correo electrónico");

        if (huespedRepository.existsByTelefonoAndEstadoRegistro(request.telefono(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un huésped activo con el mismo teléfono");
    }

    private void validarCambiosUnicos(HuespedRequest request, Long id){
        if (huespedRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email().toLowerCase(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un huésped activo con el mismo correo electrónico");

        if (huespedRepository.existsByTelefonoAndEstadoRegistroAndIdNot(request.telefono().toLowerCase(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un huésped activo con el mismo teléfono");
    }

    private void validarHuespedActualizarOEliminar(Long id){
        try {
            reservaClient.validarHuespedNoTieneReservasActivas(id);
        } catch (FeignException.Conflict e){
            throw new ConflictoException("El huésped con id: " + id + " tiene reservas activas y no puede ser eliminado o actualizado");
        }
    }
}
