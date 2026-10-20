package com.pedromax.huespedes.service;

import com.pedromax.commons.dto.habitaciones.huespedes.HuespedRequest;
import com.pedromax.commons.dto.habitaciones.huespedes.HuespedResponse;
import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.RecursoNoEncontradoException;
import com.pedromax.huespedes.entity.Huesped;
import com.pedromax.huespedes.mapper.HuespedMapper;
import com.pedromax.huespedes.repository.HuespedRepository;
import com.pedromax.huespedes.utils.ServiceUtils;
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

    @Override
    public HuespedResponse obtenerHuespedPorIdSinEstado(Long id) {return huespedMapper.entidadAResponse(obtenerHuesped(id));}

    @Override
    @Transactional(readOnly = true)
    public List<HuespedResponse> listar() {
        log.info("Listando todos los huespedes...");
        return huespedRepository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(huespedMapper::entidadAResponse).toList();
    }

    @Override
    public HuespedResponse obtenerPorId(Long id) {
        return huespedMapper.entidadAResponse(obtenerHuespedActivo(id));
    }

    @Override
    public HuespedResponse registrar(HuespedRequest request) {
        validarRegistroUnico(request);

        Huesped huesped = huespedMapper.requestAEntidad(request);

        huespedRepository.saveAndFlush(huesped);

        log.info("Huesped registrado con éxito");

        return huespedMapper.entidadAResponse(huesped);
    }

    @Override
    public HuespedResponse actualizar(HuespedRequest request, Long id) {

        Huesped huesped = obtenerHuespedActivo(id);
        validarActualizarUnico(request, id);

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

        log.info("Huesped actualizado con éxito");

        return huespedMapper.entidadAResponse(huesped);
    }

    @Override
    public void eliminar(Long id) {
        Huesped huesped = obtenerHuespedActivo(id);

        huesped.eliminar();

        huespedRepository.saveAndFlush(huesped);

        log.info("El huesped con id:{} ha sido eliminado 💀", id);
    }

    private Huesped obtenerHuesped(Long id){
        return ServiceUtils.obtenerEntidadOExcepcion(
                huespedRepository,
                id,
                Huesped.class
        );
    }

    private Huesped obtenerHuespedActivo(Long id){
        log.info("Obteniendo huesped activo con id: {}", id);
        return huespedRepository.findByIdAndEstadoRegistro(
                id, EstadoRegistro.ACTIVO
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Huesped no encontrado con id: " + id
                )
        );
    }

    private void validarRegistroUnico(HuespedRequest request){
        if(huespedRepository.existsByEmailIgnoreCaseOrTelefonoAndEstadoRegistro(
                request.email().toLowerCase(),
                request.telefono(),
                EstadoRegistro.ACTIVO
        ))
            throw new ConflictoException("Ya existe un huesped registrado con el mismo correo electrónico y número de teléfono");
    }

    private void validarActualizarUnico(HuespedRequest request, Long id){
        if(huespedRepository.existsByEmailIgnoreCaseOrTelefonoAndEstadoRegistroAndIdNot(
                request.email().toLowerCase(),
                request.telefono(),
                EstadoRegistro.ACTIVO,
                id
        ))
            throw new ConflictoException("Ya existe un huesped registrado con el mismo correo electrónico y número de teléfono");
    }
}
