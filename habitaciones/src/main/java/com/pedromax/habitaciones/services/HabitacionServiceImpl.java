package com.pedromax.habitaciones.services;

import com.pedromax.commons.clients.ReservaClient;
import com.pedromax.commons.dto.habitaciones.HabitacionRequest;
import com.pedromax.commons.dto.habitaciones.HabitacionResponse;
import com.pedromax.commons.dto.reservas.ReservaResponse;
import com.pedromax.commons.enums.EstadoHabitacion;
import com.pedromax.commons.enums.EstadoRegistro;
import com.pedromax.commons.enums.TipoHabitacion;
import com.pedromax.commons.exceptions.ConflictoException;
import com.pedromax.commons.exceptions.DatoInvalidoException;
import com.pedromax.commons.exceptions.RecursoNoEncontradoException;
import com.pedromax.habitaciones.entities.Habitacion;
import com.pedromax.habitaciones.mapper.HabitacionMapper;
import com.pedromax.habitaciones.repositories.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final HabitacionMapper habitacionMapper;
    private final ReservaClient reservaClient;

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionResponse> listar() {
        log.info("Listando todas las habitaciones activas");

        return habitacionRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(habitacionMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionResponse obtenerPorId(Long id) {
        return habitacionMapper.entidadAResponse(obtenerHabitacionActivaPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionResponse obtenerHabitacionPorIdSinEstado(Long id) {
        log.info("Buscando habitación sin estado con ID {}", id);

        return habitacionMapper.entidadAResponse(habitacionRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Habitación sin estado no encontrada con ID: " + id)));
    }

    @Override
    public HabitacionResponse registrar(HabitacionRequest request) {
        log.info("Registrando nueva habitación con el número: {}", request.numero());

        validarDatosUnicos(request);
        Habitacion habitacion = habitacionMapper.requestAEntidad(request,
                TipoHabitacion.obtenerTipoHabitacionPorDescripcion(request.tipoHabitacion()));
        habitacionRepository.saveAndFlush(habitacion);

        log.info("Nueva habitación registrada con el número: {}", habitacion.getNumero());
        return habitacionMapper.entidadAResponse(habitacion);
    }

    @Override
    public HabitacionResponse actualizar(HabitacionRequest request, Long id) {
        Habitacion habitacion = obtenerHabitacionActivaPorId(id);

        if (habitacion.getEstadoHabitacion() == EstadoHabitacion.OCUPADA) {
            if (habitacion.getCapacidad() < obtenerPersonasDeReservaVigente(id)) {
                throw new ConflictoException("La capacidad no puede ser menor que el número de personas de la reserva vigente");
            }
        }

        validarCambiosUnicos(request, id);
        log.info("Actualizando habitación con id: {}", id);

        habitacion.actualizar(
                request.numero(),
                request.precio(),
                request.capacidad(),
                TipoHabitacion.obtenerTipoHabitacionPorDescripcion(request.tipoHabitacion()));
        habitacionRepository.saveAndFlush(habitacion);

        log.info("Habitación con id {} actualizada correctamente", id);
        return habitacionMapper.entidadAResponse(habitacion);
    }

    @Override
    public void actualizarEstadoHabitacion(Long idHabitacion, Long idEstadoHabitacion) {
        Habitacion habitacion = obtenerHabitacionActivaPorId(idHabitacion);
        log.info("Actualizando estado de la habitación con id: {}", idHabitacion);

        //== Verificar si el cambio es manual
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean esCambioManual = (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken));

        habitacion.actualizarEstadoHabitacion(
                EstadoHabitacion.obtenerEstadoHabitacionPorCodigo(idEstadoHabitacion), esCambioManual);
        habitacionRepository.saveAndFlush(habitacion);
        log.info("Estado de la habitación {} actualizando correctamente", idHabitacion);
    }

    @Override
    public void eliminar(Long id) {
        Habitacion habitacion = obtenerHabitacionActivaPorId(id);

        if (habitacion.getEstadoHabitacion() == EstadoHabitacion.OCUPADA) {
            throw new ConflictoException("No se puede eliminar una habitación que se encuentra actualmente ocupada");
        }
        log.info("Eliminando la habitación con ID: {}", id);

        habitacion.eliminar();
        habitacionRepository.saveAndFlush(habitacion);

        log.info("La habitación ha sido eliminada exitósamente");
    }

    private Habitacion obtenerHabitacionActivaPorId(Long id) {
        log.info("Buscando habitación activa con ID {}", id);

        return habitacionRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Habitación activa no encontrada con ID: " + id));
    }

    private void validarDatosUnicos(HabitacionRequest request) {
        log.info("Validando número de habitación único...");

        if (habitacionRepository.existsByNumeroAndEstadoRegistro(request.numero(), EstadoRegistro.ACTIVO))
            throw new DatoInvalidoException("Ya existe una habitación activa registrada con el número: " + request.numero());
    }

    private void validarCambiosUnicos(HabitacionRequest request, Long id) {
        log.info("Validando cambio en número de habitación único...");

        if (habitacionRepository.existsByNumeroAndEstadoRegistroAndIdNot(request.numero(), EstadoRegistro.ACTIVO, id))
            throw new DatoInvalidoException("Ya existe una habitación activa registrada con el número: " + request.numero());

    }

    private Integer obtenerPersonasDeReservaVigente(Long id){
        log.info("Buscando numero de personas en la reserva con id {} en el servicio remoto...", id);
        ReservaResponse reserva = reservaClient.obtenerNumeroPersonasReserva(id);
        return reserva.numPersonas();
    }
}
